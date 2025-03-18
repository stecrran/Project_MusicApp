package com.tus.musicapp.controller.test;

import com.tus.musicapp.controller.AdminController;
import com.tus.musicapp.model.Role;
import com.tus.musicapp.model.User;
import com.tus.musicapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminController adminController;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoles(Set.of());
    }

    @Test
    void registerUser_ShouldThrowException_WhenRolesAreEmpty() {
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            adminController.registerUser(user);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, ((ResponseStatusException) exception).getStatus());
        assertTrue(exception.getMessage().contains("User roles must be provided"));
    }

    @Test
    void registerUser_ShouldRegisterUser_WhenValidRequest() {
        user.setRoles(Set.of(Role.ADMIN));
        when(userService.registerUser(any(User.class))).thenReturn(user);

        User result = adminController.registerUser(user);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userService, times(1)).registerUser(any(User.class));
    }
    
    @Test
    void registerUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        user.setRoles(Set.of(Role.ADMIN));

        when(userService.registerUser(any(User.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken"));

        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            adminController.registerUser(user);
        });

        assertEquals(HttpStatus.CONFLICT, ((ResponseStatusException) exception).getStatus());
        assertTrue(exception.getMessage().contains("Username already taken"),
                "Exception message should indicate username conflict");
    }

    @Test
    void getAllUsers_ShouldReturnUserList() {
        List<User> users = List.of(user);
        when(userService.getAllUsers()).thenReturn(users);

        List<User> result = adminController.getAllUsers();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void deleteUser_ShouldCallServiceDeleteMethod() {
        ResponseEntity<?> response = adminController.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser(1L);
    }
    
    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
                .when(userService).deleteUser(99L);

        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            adminController.deleteUser(99L);
        });

        assertEquals(HttpStatus.NOT_FOUND, ((ResponseStatusException) exception).getStatus());
        assertTrue(exception.getMessage().contains("User not found"),
                "Exception message should indicate user does not exist");
    }
    
    @Test
    void deleteUser_AdminCannotDeleteThemselves() {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Admins cannot delete themselves"))
                .when(userService).deleteUser(2L);

        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            adminController.deleteUser(2L);
        });

        assertEquals(HttpStatus.FORBIDDEN, ((ResponseStatusException) exception).getStatus());
        assertTrue(exception.getMessage().contains("Admins cannot delete themselves"),
                "Exception message should indicate self-deletion restriction");
    }
}