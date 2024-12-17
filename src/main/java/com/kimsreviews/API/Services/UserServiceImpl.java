package com.kimsreviews.API.Services;

import com.kimsreviews.API.DTO.UserDTO;
import com.kimsreviews.API.Exceptions.UserNotFoundEXceptions;
import com.kimsreviews.API.Repository.UserRepo;
import com.kimsreviews.API.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserInterface {

    private final UserRepo userRepo;
    private final JavaMailSender emailSender;
    private final ImageUploadService imageUploadService;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, JavaMailSender emailSender, ImageUploadService imageUploadService) {
        this.userRepo = userRepo;
        this.emailSender = emailSender;
        this.imageUploadService = imageUploadService;
    }

    @Override
    public UserDTO createUserDTO(UserDTO userDTO, List<MultipartFile> files) throws Exception {
        System.out.println("Creating user...");
        User user = mapToEntity(userDTO);

        if (files != null && !files.isEmpty()) {
            System.out.println("Uploading user image...");
            MultipartFile imageFile = files.get(0);
            String imageUrl = imageUploadService.uploadImage(imageFile);
            System.out.println("Image URL: " + imageUrl);
            user.setUserImageUrl(imageUrl);
        }

        // Encrypt password
        System.out.println("Encrypting password...");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        System.out.println("Saving user to database...");
        User savedUser = userRepo.save(user);
        System.out.println("User saved with ID: " + savedUser.getId());

        return mapToDTO(savedUser);
    }

    @Override
    public UserDTO createUserDTO(UserDTO userDTO) {
        return null;
    }

    @Override
    public List<UserDTO> getAllUser() {
        System.out.println("Fetching all users...");
        return userRepo.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(int id) {
        System.out.println("Fetching user by ID: " + id);
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundEXceptions("User not found"));
        return mapToDTO(user);
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
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        user.setIsVerified(false);
        User updatedUser = userRepo.save(user);
        return mapToDTO(updatedUser);
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
        return false;
    }

    @Override
    public boolean resetPassword(String phoneNumber) {
        return false;
    }

    @Override
    public UserDTO getUserDetailsByPhoneNumber(String phoneNumber) {
        return null;
    }

    private UserDTO mapToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId((long) user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setUserImageUrl(user.getUserImageUrl());
        return userDTO;
    }

    private User mapToEntity(UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setPassword(userDTO.getPassword());
        user.setUserImageUrl(userDTO.getUserImageUrl());
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
