package com.tus.musicapp.service.test;

import com.tus.musicapp.model.Role;
import com.tus.musicapp.model.User;
import com.tus.musicapp.repos.UserRepository;
import com.tus.musicapp.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private SecurityContext securityContext;

	@Mock
	private Authentication authentication;

	@InjectMocks
	private UserService userService;

	private User testUser;
	private User adminUser;

	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setId(1L);
		testUser.setUsername("testuser");
		testUser.setPassword("password123");

		// Use a mutable HashSet instead of Set.of()
		testUser.setRoles(new HashSet<>(Set.of(Role.SPOTIFY_USER)));

		adminUser = new User();
		adminUser.setId(2L);
		adminUser.setUsername("admin");
		adminUser.setPassword("adminpass");

		// Use a mutable HashSet instead of Set.of()
		adminUser.setRoles(new HashSet<>(Set.of(Role.ADMIN, Role.SPOTIFY_USER)));
	}

	@Test
	void registerUser_SuccessfulRegistration() {
		when(userRepository.existsByUsername("testuser")).thenReturn(false);
		when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(testUser);

		User registeredUser = userService.registerUser(testUser);

		assertNotNull(registeredUser);
		assertEquals("testuser", registeredUser.getUsername());
		assertEquals("encodedPassword", registeredUser.getPassword());
		assertTrue(registeredUser.getRoles().contains(Role.SPOTIFY_USER));

		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void registerUser_UsernameAlreadyTaken_ThrowsException() {
		when(userRepository.existsByUsername("testuser")).thenReturn(true);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> userService.registerUser(testUser));

		assertEquals(HttpStatus.CONFLICT, exception.getStatus());
		assertEquals("Username already taken", exception.getReason());
	}

	@Test
	void registerUser_NoRolesAssigned_ThrowsException() {
		testUser.setRoles(null);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> userService.registerUser(testUser));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
		assertEquals("At least one role must be assigned.", exception.getReason());
	}

	@Test
	void getAllUsers_ReturnsUserList() {
		when(userRepository.findAll()).thenReturn(List.of(testUser, adminUser));

		List<User> users = userService.getAllUsers();

		assertEquals(2, users.size());
		verify(userRepository, times(1)).findAll();
	}

	@Test
	void deleteUser_SuccessfulDeletion() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(adminUser));
		when(authentication.getName()).thenReturn("admin");
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		userService.deleteUser(1L);

		verify(userRepository, times(1)).delete(testUser);
	}

	@Test
	void deleteUser_UserNotFound_ThrowsException() {
		// Mock current authenticated user (so "Current user not found" error doesn't occur)
		when(authentication.getName()).thenReturn("admin");
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		// Mock that current user exists
		when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

		// Mock that the user to be deleted does NOT exist
		when(userRepository.findById(3L)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> userService.deleteUser(3L));

		// Ensure correct exception message
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
		assertEquals("User not found", exception.getReason()); // Now it will match
	}

	@Test
	void deleteUser_AdminCannotDeleteThemselves_ThrowsException() {
		// Mock authentication context to return "admin"
		when(authentication.getName()).thenReturn("admin");
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		// Mock current user retrieval to return the admin user
		when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

		// Remove this if it's not used inside `deleteUser()`
		// when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

		// Execute the method and assert
		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> userService.deleteUser(2L));

		assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
		assertEquals("Admins cannot delete themselves.", exception.getReason());
	}

	@Test
	void updateUser_SuccessfulUpdate() {
		User updatedUser = new User();
		updatedUser.setPassword("newPassword");
		updatedUser.setRoles(Set.of(Role.SPOTIFY_USER));

		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(adminUser));
		when(authentication.getName()).thenReturn("admin");
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(userRepository.save(any(User.class))).thenReturn(testUser);

		User result = userService.updateUser(1L, updatedUser);

		assertNotNull(result);
		assertEquals("encodedNewPassword", result.getPassword());
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void updateUser_AdminCannotRemoveTheirOwnAdminRole_ThrowsException() {
		User updatedUser = new User();
		updatedUser.setRoles(Set.of(Role.SPOTIFY_USER));

		when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
		when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
		when(authentication.getName()).thenReturn("admin");
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> userService.updateUser(2L, updatedUser));

		assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
		assertEquals("Admins cannot remove their own admin role.", exception.getReason());
	}
}
