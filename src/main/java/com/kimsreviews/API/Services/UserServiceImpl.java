package com.kimsreviews.API.Services;

import com.kimsreviews.API.DTO.UserDTO;
import com.kimsreviews.API.Exceptions.UserNotFoundEXceptions;
import com.kimsreviews.API.Repository.UserRepo;
import com.kimsreviews.API.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserInterface {

    private final UserRepo userRepo;
    private final ImageUploadService imageUploadService;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, ImageUploadService imageUploadService, UserMapper userMapper) {
        this.userRepo = userRepo;
        this.imageUploadService = imageUploadService;
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO createUserDTO(UserDTO userDTO, List<MultipartFile> files) throws Exception {
        return null;
    }

    @Override
    public UserDTO createUserDTO(UserDTO userDTO, MultipartFile userImage) throws Exception {
        // Log the incoming request
        System.out.println("Creating user with details: " + userDTO);

        // Check if user already exists by email
        if (userRepo.existsByEmail(userDTO.getEmail())) {
            throw new UserCreationException("User creation failed: Email is already in use.");
        }

        // Map DTO to Entity
        User user = userMapper.toEntity(userDTO);

        // Handle image upload if file is provided
        if (userImage != null && !userImage.isEmpty()) {
            try {
                String imageUrl = imageUploadService.uploadImage(userImage); // handle image upload in service
                user.setUserImageUrl(imageUrl);
                System.out.println("Image uploaded successfully. URL: " + imageUrl);
            } catch (Exception e) {
                throw new UserCreationException("User creation failed: Image upload failed. " + e.getMessage());
            }
        }

        // Encrypt the password before saving
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));  // Encrypt password here

        try {
            // Save user to the database
            User savedUser = userRepo.save(user);
            System.out.println("User saved successfully with ID: " + savedUser.getId());

            // Return the saved user as DTO
            return userMapper.toDTO(savedUser);

        } catch (DataIntegrityViolationException e) {
            // Catch database constraint violations (e.g., unique constraint violation)
            throw new UserCreationException("User creation failed: Database integrity violation. " + e.getMessage());
        } catch (Exception e) {
            throw new UserCreationException("User creation failed: " + e.getMessage());
        }
    }


    @Override
    public UserDTO createUserDTO(UserDTO userDTO) {
        return userDTO;
    }

    @Override
    public List<UserDTO> getAllUser() {
        System.out.println("Fetching all users...");
        return userRepo.findAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(int id) {
        System.out.println("Fetching user by ID: " + id);
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundEXceptions("User not found"));
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO, int id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundEXceptions("User not found"));

        // Update user fields
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAbove18(userDTO.isAbove18());

        // Encrypt the new password if it is being updated
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        user.setEmail(userDTO.getEmail());
        user.setIsVerified(false);

        try {
            // Save updated user
            User updatedUser = userRepo.save(user);
            return userMapper.toDTO(updatedUser);
        } catch (Exception e) {
            System.err.println("Error occurred while updating user: " + e.getMessage());
            throw new RuntimeException("Error updating user: " + e.getMessage());
        }
    }

    @Override
    public void updateUserPasswords() {

    }

    @Override
    public void deleteUserId(int id) {
        System.out.println("Deleting user with ID: " + id);
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundEXceptions("User not found"));
        userRepo.delete(user);
        System.out.println("User deleted successfully.");
    }

    @Override
    public boolean verifyUserCredentials(UserDTO userDTO) {
        Optional<User> userOptional = userRepo.findByEmail(userDTO.getEmail());
        if (userOptional.isPresent()) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            return passwordEncoder.matches(userDTO.getPassword(), userOptional.get().getPassword());
        }
        return false;
    }


    @Override
    public boolean resetPassword(String phoneNumber) {
        return false;
    }

    @Override
    public UserDTO getUserDetailsByPhoneNumber(String phoneNumber) {
        User user = userRepo.findByPhoneNumber(phoneNumber);
        if (user != null) {
            return userMapper.toDTO(user);
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        User user = userRepo.findByPhoneNumber(phoneNumber);

        if (user == null) {
            System.err.println("User not found with phone number: " + phoneNumber);
            throw new UsernameNotFoundException("User not found with phone number: " + phoneNumber);
        }

        System.out.println("User found: " + user.getPhoneNumber());

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getPhoneNumber())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

}
