package com.kimsreviews.API.Repository;

import com.kimsreviews.API.DTO.UserDTO;
import com.kimsreviews.API.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface  UserRepo extends JpaRepository<User,Integer> {
    User findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    static List<User> findByUsernameContainingIgnoreCase(String username) {
        return null;
    }
}
