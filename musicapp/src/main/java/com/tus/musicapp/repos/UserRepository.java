package com.tus.musicapp.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tus.musicapp.model.User;

import java.util.Optional;

// Spring Data JPA repository. Interface used to manage database interactions related to the User entity
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

}
