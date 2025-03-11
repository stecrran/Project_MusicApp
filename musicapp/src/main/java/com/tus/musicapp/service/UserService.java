package com.tus.musicapp.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tus.musicapp.exceptions.UsernameAlreadyExistsException;
import com.tus.musicapp.model.Role;
import com.tus.musicapp.model.User;
import com.tus.musicapp.repos.UserRepository;

import org.springframework.security.core.Authentication;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostConstruct
    public void ensureAdminExists() {
        String adminUsername = "Admin";
        String adminPassword = "Admin";

        if (!userRepository.existsByUsername(adminUsername)) {
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(Role.ADMIN);
            adminRoles.add(Role.SPOTIFY_USER); // Admins always get SPOTIFY_USER

            adminUser.setRoles(adminRoles);
            userRepository.save(adminUser);
            System.out.println("✅ Default Admin User Created: " + adminUsername);
        } else {
            System.out.println("✅ Admin User Already Exists: " + adminUsername);
        }
    }
    
    public User registerUser(User user) {
        // Normalize the username to lower case before checking and saving
         user.setUsername(user.getUsername().toLowerCase());
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
        
        // Encrypt the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Use the helper method to assign roles correctly
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            user.setRoles(assignRoles(user.getRoles()));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A role must be selected.");
        }
        
        return userRepository.save(user);
    }
    

    //For the users table in the front end
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found"));

        if (currentUser.getRoles().contains(Role.ADMIN) && currentUser.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admins cannot delete themselves.");
        }

        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // **First, clear roles before deleting the user**
        userToDelete.getRoles().clear();
        userRepository.save(userToDelete);

        // **Then delete the user**
        userRepository.delete(userToDelete);
    }



    // Update user details
    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found"));
    
        // Prevent admin from removing their own admin role
        if (currentUser.getId().equals(id) && currentUser.getRoles().contains(Role.ADMIN) && 
            (updatedUser.getRoles() == null || !updatedUser.getRoles().contains(Role.ADMIN))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admins cannot remove their own admin role.");
        }
    
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
    
        if (updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
            existingUser.setRoles(assignRoles(updatedUser.getRoles()));
        }
    
        return userRepository.save(existingUser);
    }
    
        
    //Helper method to assign roles to user
    private Set<Role> assignRoles(Set<Role> selectedRoles) {
    Set<Role> roles = new HashSet<>();
    if (selectedRoles.contains(Role.ADMIN)) {
        roles.add(Role.SPOTIFY_USER);
        roles.add(Role.ADMIN);
    }

    return roles;
}

}
