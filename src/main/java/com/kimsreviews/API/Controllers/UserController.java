package com.kimsreviews.API.Controllers;

import com.kimsreviews.API.DTO.UserDTO;
import com.kimsreviews.API.Services.FileStorageService;
import com.kimsreviews.API.Services.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/")
@CrossOrigin("http://localhost:4200")
public class UserController {

    private final UserInterface userService;
    private final FileStorageService fileStorageService;

    @Autowired
    public UserController(UserInterface userService, FileStorageService fileStorageService) {
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserDTO>> getUsers() {
        return new ResponseEntity<>(userService.getAllUser(), HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> userDetails(@PathVariable int id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/user/upload/documents")
    public ResponseEntity<List<String>> getUploadedDocuments() {
        List<String> documents = fileStorageService.getUploadedDocuments();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/user/details")
    public ResponseEntity<UserDTO> getUserDetailsByPhoneNumber(@RequestParam String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        UserDTO userDetails = userService.getUserDetailsByPhoneNumber(phoneNumber);

        if (userDetails != null) {
            return ResponseEntity.ok(userDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/user/create")
    public ResponseEntity<?> createUser(@ModelAttribute UserDTO userDTO, @RequestParam("userImage") MultipartFile userImage) {
        try {
            // Validate userDTO fields
            if (userDTO.getPhoneNumber().length() < 10) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Phone number must be at least 10 digits long."));
            }

            String emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
            if (!userDTO.getEmail().matches(emailRegex)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Invalid email format."));
            }

            // Check if phone number already exists
            if (userService.getUserDetailsByPhoneNumber(userDTO.getPhoneNumber()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("Phone number is already registered."));
            }

            // Save the image and set the image URL in userDTO
            String imagePath = saveImage(userImage);
            userDTO.setUserImageUrl(imagePath);

            // Call userService method to create userDTO
            UserDTO createdUser = userService.createUserDTO(userDTO);

            // Return appropriate response based on successful creation or error
            if (createdUser != null) {
                String jwtToken = generateJwtToken(createdUser);
                return ResponseEntity.ok().header("Authorization", jwtToken).body(createdUser);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Method to save uploaded image
    private String saveImage(MultipartFile image) throws IOException {
        if (image.isEmpty()) {
            return null;
        }

        byte[] bytes = image.getBytes();
        Path path = Paths.get("uploads/" + image.getOriginalFilename());
        Files.write(path, bytes);

        return path.toString();
    }

    @PutMapping("/user/{id}/update")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO, @PathVariable("id") int userId) {
        UserDTO response = userService.updateUser(userDTO, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/user/{id}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable("id") int userId) {
        userService.deleteUserId(userId);
        return ResponseEntity.ok("User deleted");
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDTO userDTO) {
        if (userService.verifyUserCredentials(userDTO)) {
            String jwtToken = generateJwtToken(userDTO);
            return ResponseEntity.ok().body(new LoginResponse("Login successful", jwtToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Login failed. Kindly check your credentials or register."));
        }
    }

    @PostMapping("/user/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody UserDTO userDTO) {
        try {
            boolean result = userService.resetPassword(userDTO.getPhoneNumber());
            if (result) {
                // Replace with your actual email sending logic
                return ResponseEntity.ok().body(new ResponseMessage("Password reset instructions sent to your email."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Unable to reset password."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error processing password reset request."));
        }
    }

    private String generateJwtToken(UserDTO userDTO) {
        // Replace with your actual JWT token generation logic
        return "your-generated-jwt-token";
    }

    private static class LoginResponse {
        private String message;
        private String token;

        public LoginResponse(String message, String token) {
            this.message = message;
            this.token = token;
        }

        public String getMessage() {
            return message;
        }

        public String getToken() {
            return token;
        }
    }

    private static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private static class ResponseMessage {
        private String message;

        public ResponseMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    // Other methods as needed
}
