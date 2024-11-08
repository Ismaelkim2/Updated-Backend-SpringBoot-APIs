package com.kimsreviews.API.Services;

import com.kimsreviews.API.DTO.UserDTO;
import com.kimsreviews.API.Exceptions.UserNotFoundEXceptions;
import com.kimsreviews.API.Repository.UserRepo;
import com.kimsreviews.API.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserInterface, UserDetailsService {

    private final UserRepo userRepo;
    private final JavaMailSender emailSender;
    private final FileStorageService fileStorageService;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, JavaMailSender emailSender, FileStorageService fileStorageService, UserMapper userMapper) {
        this.userRepo = userRepo;
        this.emailSender = emailSender;
        this.fileStorageService = fileStorageService;
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO createUserDTO(UserDTO userDTO, List<MultipartFile> files) {
        User user = mapToEntity(userDTO);

        // Save files if provided
        if (files != null && !files.isEmpty()) {
            user.setDocumentUrls(fileStorageService.storeFiles(files));
        }

        userRepo.save(user);
        return mapToDTO(user);
    }

    @Override
    public UserDTO createUserDTO(UserDTO userDTO) {
        User user = mapToEntity(userDTO);
        userRepo.save(user);
        return mapToDTO(user);
    }

    @Override
    public List<UserDTO> getAllUser() {
        return userRepo.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(int id) {
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
        User updatedUser = userRepo.save(user);
        return mapToDTO(updatedUser);
    }

    @Override
    public void updateUserPasswords() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        List<User> users = userRepo.findAll();
        for (User user : users) {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);
            userRepo.save(user);
        }
    }

    @Override
    public void deleteUserId(int id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundEXceptions("User not found"));
        userRepo.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        System.out.println("Searching for user with phone number: " + phoneNumber);
        User user = userRepo.findByPhoneNumber(phoneNumber);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with phone number: " + phoneNumber);
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getPhoneNumber())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }


    @Override
    public boolean verifyUserCredentials(UserDTO userDTO) {
        User storedUser = userRepo.findByPhoneNumber(userDTO.getPhoneNumber());
        return storedUser != null && storedUser.getPassword().equals(userDTO.getPassword());
    }

    @Override
    public boolean resetPassword(String phoneNumber) {
        User user = userRepo.findByPhoneNumber(phoneNumber);
        if (user != null) {
            // Generate a password reset token and send it via email
            String resetToken = generateResetToken();
            sendResetTokenEmail(user.getEmail(), resetToken);
            user.setResetToken(resetToken);
            userRepo.save(user);
            return true;
        }
        return false;
    }

    @Override
    public UserDTO getUserDetailsByPhoneNumber(String phoneNumber) {
        User user = userRepo.findByPhoneNumber(phoneNumber);
        return user != null ? mapToDTO(user) : null;
    }

    private UserDTO mapToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId((long) user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setAbove18(user.isAbove18());
        userDTO.setPassword(user.getPassword());
        userDTO.setUserImageUrl(user.getUserImageUrl());
        userDTO.setDocumentUrls(user.getDocumentUrls());
        return userDTO;
    }

    private User mapToEntity(UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAbove18(userDTO.isAbove18());
        user.setPassword(userDTO.getPassword());
        user.setUserImageUrl(userDTO.getUserImageUrl());
        return user;
    }

    private String generateResetToken() {
        // Generate a secure token for password reset
        return " "; // Replace with actual token generation logic
    }

    private void sendResetTokenEmail(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Token");
        message.setText("Your password reset token is: " + token);
        emailSender.send(message);
    }
}
