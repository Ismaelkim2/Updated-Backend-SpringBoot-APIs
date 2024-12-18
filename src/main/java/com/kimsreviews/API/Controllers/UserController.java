package com.kimsreviews.API.Controllers;

import com.kimsreviews.API.DTO.UserDTO;
import com.kimsreviews.API.Implementations.JwtTokenProvider;
import com.kimsreviews.API.Implementations.PostNotFoundException;
import com.kimsreviews.API.Implementations.UnauthorizedException;
import com.kimsreviews.API.Services.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "https://brishkimecoeggs.onrender.com")
@RequestMapping("/api")
public class UserController {

    private final UserInterface userService;
    private final FileStorageService fileStorageService;
    private final ImageUploadService imageUploadService;

    @Autowired
    @Lazy
    private PostServiceImpl postService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserInterface userService, FileStorageService fileStorageService, PostServiceImpl postService, ImageUploadService imageUploadService) {
        this.userService = userService;
        this.fileStorageService = fileStorageService;
        this.postService = postService;
        this.imageUploadService=imageUploadService;
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserDTO>> getUsers() {
        return new ResponseEntity<>(userService.getAllUser(), HttpStatus.OK);
    }

    @GetMapping("/user/validate-token")
    public ResponseEntity<ResponseMessage> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || authHeader.isEmpty() || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage("Missing or invalid Authorization header"));
        }

        String token = authHeader.substring("Bearer ".length());

        try {
            Claims claims = jwtTokenProvider.extractAllClaims(token);
            return ResponseEntity.ok(new ResponseMessage("Token is valid"));
        } catch (ExpiredJwtException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseMessage("Token has expired"));
        } catch (MalformedJwtException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage("Token is malformed"));
        } catch (JwtException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseMessage("Token is invalid"));
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("Unexpected error during token validation: ", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("Token validation failed due to server error"));
        }
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


//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
@PostMapping("/user/create")
public ResponseEntity<?> createUser(
        @ModelAttribute UserDTO userDTO,
        @RequestParam(value = "userImageUrl", required = false) MultipartFile[] userImages,
        @RequestParam(value = "userImageUrl", required = false) String userImageUrl) {
    try {
        // Validate phone number length
        if (userDTO.getPhoneNumber().length() < 10) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Phone number must be at least 10 digits long."));
        }

        // Validate email format
        String emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        if (!userDTO.getEmail().matches(emailRegex)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid email format."));
        }

        // Check if phone number is already registered
        if (userService.getUserDetailsByPhoneNumber(userDTO.getPhoneNumber()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Phone number is already registered."));
        }

        // Set default value for documentUrls if null
        if (userDTO.getDocumentUrls() == null) {
            userDTO.setDocumentUrls(new ArrayList<>());
        }

        // Handling image URLs or multi-file uploads
        if (userImages != null && userImages.length > 0) {
            // Handle multi-file upload if files are provided
            List<String> uploadedImageUrls = new ArrayList<>();
            for (MultipartFile image : userImages) {
                // Assuming a method to upload image and return its URL
                String imageUrl = imageUploadService.uploadImage(image);
                uploadedImageUrls.add(imageUrl);
            }
            userDTO.setUserImageUrl(String.join(",", uploadedImageUrls)); // Concatenate URLs as a comma-separated string
        } else if (userImageUrl != null && !userImageUrl.isEmpty()) {
            // Use the provided URL string if it's not null or empty
            userDTO.setUserImageUrl(userImageUrl);
        }

        // Call service layer to handle user creation
        UserDTO createdUser = userService.createUserDTO(userDTO);

        // Return the success response
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An error occurred while creating the user."));
    }
}

    @PutMapping("/user/{id}/update")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO, @PathVariable("id") int userId) {
        UserDTO response = userService.updateUser(userDTO, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDTO userDTO) {
        try {
            // Authenticate using phone number and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDTO.getPhoneNumber(), userDTO.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT Token
            String token = jwtTokenProvider.generateToken(authentication.getName());

            // Log success
            System.out.println("Login successful for user: " + userDTO.getPhoneNumber());

            return ResponseEntity.ok(new ResponseMessage("Login successful. Token: " + token));

        } catch (BadCredentialsException e) {
            // Log invalid credentials
            System.err.println("Invalid credentials for phone number: " + userDTO.getPhoneNumber());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseMessage("Invalid phone number or password"));
        } catch (Exception e) {
            // Handle other exceptions
            System.err.println("Error during login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("An error occurred during login"));
        }
    }


    @PostMapping("/user/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody UserDTO userDTO) {
        try {
            boolean result = userService.resetPassword(userDTO.getPhoneNumber());
            if (result) {
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
        return "7FNWfRR6MIjRJmPPU7mZBs9vR01oExTLJ978Oj10A7U=";
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
}
