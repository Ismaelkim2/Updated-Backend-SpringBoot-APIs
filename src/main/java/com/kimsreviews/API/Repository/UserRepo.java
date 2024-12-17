package com.kimsreviews.API.Repository;

import com.kimsreviews.API.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer> {
    // Method to find a user by phone number
    User findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);

    // Method to find a user by ID (use Optional<User> for safety)
    Optional<User> findById(Long id);

    // Method to check if an email exists
    boolean existsByEmail(String email);

    // Corrected method to find a user by email
    Optional<User> findByEmail(String email);  // This method should be used in the loadUserByUsername method
}
