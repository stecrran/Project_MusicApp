package com.tus.musicapp.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tus.musicapp.service.UserDetailsServiceImpl;
import com.tus.musicapp.util.JwtUtil;

// Control authorisations
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    // login requests
    public static class AuthRequest {
        public String username;
        public String password;
    }

    // responses
    public static class AuthResponse {
        public String jwt;
        public String username;
        public List<String> roles;

        public AuthResponse(String jwt, String username, List<String> roles) {
            this.jwt = jwt;
            this.username = username;
            this.roles = roles;
        }
    }

    // Authorise login
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password)
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }
        
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.username);
        final String jwt = jwtUtil.generateToken(userDetails);
        
   // Define the desired order of roles
    List<String> desiredOrder = Arrays.asList(
        "ADMIN",
        "SPOTIFY_USER"
    );

    // Extract and sort roles according to the desired order
    List<String> roles = userDetails.getAuthorities().stream()
        .map(authority -> authority.getAuthority())
        .sorted((a, b) -> {
            int aIndex = desiredOrder.indexOf(a);
            int bIndex = desiredOrder.indexOf(b);
            aIndex = aIndex == -1 ? Integer.MAX_VALUE : aIndex;
            bIndex = bIndex == -1 ? Integer.MAX_VALUE : bIndex;
            return Integer.compare(aIndex, bIndex);
        })
        .collect(Collectors.toList());
        
        return ResponseEntity.ok(new AuthResponse(jwt, userDetails.getUsername(), roles));
    }

}

