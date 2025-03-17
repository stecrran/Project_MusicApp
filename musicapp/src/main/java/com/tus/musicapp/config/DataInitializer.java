package com.tus.musicapp.config;

import com.tus.musicapp.model.Role;
import com.tus.musicapp.model.User;
import com.tus.musicapp.repos.UserRepository;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// initialise a user with each user type
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private String declareAdmin = "admin";
    private String declareSpotifyUser = "spotify_user";
    
    @Override
    public void run(String... args) throws Exception {
        // Check if an admin already exists
        if (userRepository.findByUsername(declareAdmin).isEmpty()) {
            User admin = new User();
            admin.setUsername(declareAdmin);
            // Change the default password as needed for your environment
            admin.setPassword(passwordEncoder.encode(declareAdmin));
            admin.setRoles(Set.of(Role.ADMIN, Role.SPOTIFY_USER));
            
            userRepository.save(admin);
        }

        if (userRepository.findByUsername(declareSpotifyUser).isEmpty()) {
            User spotifyUser = new User();
            spotifyUser.setUsername(declareSpotifyUser);
            // Change the default password as needed for your environment
            spotifyUser.setPassword(passwordEncoder.encode(declareSpotifyUser));
            spotifyUser.setRoles(Set.of(Role.SPOTIFY_USER));
            
            userRepository.save(spotifyUser);
        }
    }
}
