package com.tus.musicapp.service.test;

import com.tus.musicapp.model.User;
import com.tus.musicapp.model.Role; 

import com.tus.musicapp.repos.UserRepository;
import com.tus.musicapp.service.UserDetailsServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");

        // ✅ Use Role enum values instead of Strings
        testUser.setRoles(Set.of(Role.ADMIN, Role.SPOTIFY_USER));
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        // Mock repository behavior
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Call the method
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Verify results
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        
        // Check authorities
        List<String> expectedRoles = List.of("ADMIN", "SPOTIFY_USER");
        List<String> actualRoles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertTrue(actualRoles.containsAll(expectedRoles));
        assertEquals(expectedRoles.size(), actualRoles.size());

        // Verify repository method was called once
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        // Mock repository to return empty
        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        // Expect exception
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, 
            () -> userDetailsService.loadUserByUsername("unknownuser"));

        assertEquals("User not found", exception.getMessage());

        // Verify repository was called once
        verify(userRepository, times(1)).findByUsername("unknownuser");
    }

    @Test
    void loadUserByUsername_UserHasNoRoles_ReturnsUserDetailsWithNoAuthorities() {
        testUser.setRoles(Set.of()); // Use Set.of() instead of List.of()
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().isEmpty()); // No authorities assigned

        verify(userRepository, times(1)).findByUsername("testuser");
    }

}
