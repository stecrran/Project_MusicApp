package com.tus.musicapp.config;

import com.tus.musicapp.model.Role;
import com.tus.musicapp.model.User;
import com.tus.musicapp.repos.UserRepository;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private String declareAdmin = "admin";
    private String declareNetworkEngineer = "network";
    
    @Override
    public void run(String... args) throws Exception {
        // Check if an admin user already exists
        if (userRepository.findByUsername(declareAdmin).isEmpty()) {
            User admin = new User();
            admin.setUsername(declareAdmin);
            // Change the default password as needed for your environment
            admin.setPassword(passwordEncoder.encode(declareAdmin));
            admin.setRoles(Set.of(Role.ADMIN, Role.SPOTIFY_USER));
            
            userRepository.save(admin);
        }

        if (userRepository.findByUsername(declareNetworkEngineer).isEmpty()) {
            User networkEngineer = new User();
            networkEngineer.setUsername(declareNetworkEngineer);
            // Change the default password as needed for your environment
            networkEngineer.setPassword(passwordEncoder.encode(declareNetworkEngineer));
            networkEngineer.setRoles(Set.of(Role.ADMIN, Role.SPOTIFY_USER));
            
            userRepository.save(networkEngineer);
        }
    }
}
