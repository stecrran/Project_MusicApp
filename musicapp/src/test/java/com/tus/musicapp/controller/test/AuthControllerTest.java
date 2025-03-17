package com.tus.musicapp.controller.test;

import com.tus.musicapp.controller.AuthController;
import com.tus.musicapp.service.UserDetailsServiceImpl;
import com.tus.musicapp.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private AuthController.AuthRequest authRequest;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        authRequest = new AuthController.AuthRequest();
        authRequest.username = "testuser";
        authRequest.password = "password";

        userDetails = new User("testuser", "password", 
                List.of(new SimpleGrantedAuthority("ADMIN")));
    }

    @Test
    void createAuthenticationToken_ShouldReturnToken_WhenValidCredentials() throws Exception {
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("mocked-jwt-token");

        ResponseEntity<?> response = authController.createAuthenticationToken(authRequest);

        assertNotNull(response);
        assertTrue(response.getBody() instanceof AuthController.AuthResponse);
        AuthController.AuthResponse authResponse = (AuthController.AuthResponse) response.getBody();
        assertEquals("mocked-jwt-token", authResponse.jwt);
        assertEquals("testuser", authResponse.username);
        assertTrue(authResponse.roles.contains("ADMIN"));
    }

    @Test
    void createAuthenticationToken_ShouldThrowException_WhenInvalidCredentials() {
        doThrow(new BadCredentialsException("Incorrect username or password"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        Exception exception = assertThrows(Exception.class, () -> {
            authController.createAuthenticationToken(authRequest);
        });

        assertTrue(exception.getMessage().contains("Incorrect username or password"));
    }
}
