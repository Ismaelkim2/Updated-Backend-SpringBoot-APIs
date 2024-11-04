package com.kimsreviews.API.Controllers;

import com.kimsreviews.API.DTO.PostDTO;
import com.kimsreviews.API.DTO.UserDTO;
import com.kimsreviews.API.Implementations.PostNotFoundException;
import com.kimsreviews.API.Implementations.UnauthorizedException;
import com.kimsreviews.API.Repository.PostRepo;
import com.kimsreviews.API.Repository.UserRepo;
import com.kimsreviews.API.Services.PostService;
import com.kimsreviews.API.Services.PostServiceImpl;
import com.kimsreviews.API.Services.UserMapper;
import com.kimsreviews.API.models.Post;
import com.kimsreviews.API.models.User;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostServiceImpl postService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @CrossOrigin("http://localhost:4200")
    @GetMapping
    public ResponseEntity<List<PostDTO>> getPosts() {
        try {
            List<PostDTO> postDTOs = postService.getAllPosts();
            return ResponseEntity.ok(postDTOs);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 error
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        PostDTO postDTO = postService.getPostById(id);
        if (postDTO != null) {
            return ResponseEntity.ok(postDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('user') or hasRole('admin')")
    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestParam("title") String title,
            @RequestParam("productType") String productType,
            @RequestParam(value = "poultryType", required = false) String poultryType,
            @RequestParam(value = "weight", required = false) Double weight,
            @RequestParam(value = "livestockType", required = false) String livestockType,
            @RequestParam(value = "livestockDescription", required = false) String livestockDescription,
            @RequestParam(value = "age", required = false) Integer age,
            @RequestParam(value = "salesAmount", required = false) Double salesAmount,
            @RequestParam("image") MultipartFile image,
            @RequestParam("createdBy") String createdBy,
            @RequestParam("userId") Long userId) {

        try {
            // Create PostDTO object and set properties
            PostDTO postDTO = new PostDTO();
            postDTO.setTitle(title);
            postDTO.setProductType(productType);
            postDTO.setPoultryType(poultryType);
            postDTO.setWeight(weight);
            postDTO.setLivestockType(livestockType);
            postDTO.setLivestockDescription(livestockDescription);
            postDTO.setAge(age);
            postDTO.setSalesAmount(salesAmount);
            postDTO.setCreatedBy(createdBy);

            // Set UserDTO
            UserDTO userDTO = new UserDTO();
            userDTO.setId(userId);
            postDTO.setUserDTO(userDTO); // Use setUserDTO to align with DTO in service layer

            // Create post
            PostDTO createdPostDTO = postService.createPost(postDTO, image);

            // Return success response
            return ResponseEntity.ok(createdPostDTO);

        } catch (IllegalArgumentException e) {
            // Return error response if user is not found
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found: " + e.getMessage());

        } catch (IOException e) {
            // Return error response if image handling fails
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image processing error: " + e.getMessage());

        } catch (Exception e) {
            // Return generic error response for unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id,
                                              @RequestParam("title") String title,
                                              @RequestParam("productType") String productType,
                                              @RequestParam(value = "poultryType", required = false) String poultryType,
                                              @RequestParam(value = "weight", required = false) Double weight,
                                              @RequestParam(value = "livestockType", required = false) String livestockType,
                                              @RequestParam(value = "livestockDescription", required = false) String livestockDescription,
                                              @RequestParam(value = "age", required = false) Integer age,
                                              @RequestParam(value = "salesAmount", required = false) Double salesAmount,
                                              @RequestParam(value = "image", required = false) MultipartFile image,
                                              @RequestParam(value = "userId", required = false) Long userId) throws IOException {

        PostDTO postDTO = new PostDTO();
        postDTO.setTitle(title);
        postDTO.setProductType(productType);
        postDTO.setPoultryType(poultryType);
        postDTO.setWeight(weight);
        postDTO.setLivestockType(livestockType);
        postDTO.setLivestockDescription(livestockDescription);
        postDTO.setAge(age);
        postDTO.setSalesAmount(salesAmount);

        if (userId != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(userId);
            postDTO.setUserDTO(userDTO);
        }

        PostDTO updatedPostDTO;
        try {
            updatedPostDTO = postService.updatePost(id, postDTO, image);
        } catch (PostNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        return ResponseEntity.ok(updatedPostDTO);
    }

    //    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, @RequestParam Long userId) {
        Optional<Post> post = postRepo.findById(id);
        System.out.println("Deleting post with ID: " + id + " by user: " + userId);

        if (post.isEmpty()) {
            System.out.println("post not found with id:"+ id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
//        Post existingPost = post.get();
        // Convert Long userId to int and compare with the post owner's ID
//        if (existingPost.getUser().getId() != userId.intValue()) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // User is not authorized
//        }
        postService.deletePost(id, userId);
        return ResponseEntity.noContent().build();
    }


    @Transactional
    @PostMapping("/{postId}/likes")
    public ResponseEntity<PostDTO> incrementLikes(
            @PathVariable Long postId,
            @RequestBody Long userId) {
        Optional<Post> optionalPost = postRepo.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            Optional<User> optionalUser = userRepo.findById(Math.toIntExact(userId));
            if (optionalUser.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            User user = optionalUser.get();

            // Check if the user has already liked the post
            if (post.getLikedBy().contains(user)) {
                return ResponseEntity.badRequest().build(); // User already liked the post
            }

            post.getLikedBy().add(user);
            post.setLikes(post.getLikes() + 1);

            Post updatedPost = postRepo.save(post);
            PostDTO postDTO = convertToDTO(updatedPost);
            return ResponseEntity.ok(postDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//    private PostDTO convertToDTO(Post post) {
//        return modelMapper.map(post, PostDTO.class);
//    }
    @PostMapping("/{id}/views")
    public ResponseEntity<Void> incrementViews(@PathVariable Long id) {
        postService.incrementViews(id);
        return ResponseEntity.ok().build();
    }

    private String saveImage(MultipartFile image) {
        if (image.isEmpty()) {
            return null;
        }
        try {
            byte[] bytes = image.getBytes();
            Path path = Paths.get("uploads/" + image.getOriginalFilename());
            Files.write(path, bytes);
            return path.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PostDTO convertToDTO(Post post) {
        PostDTO postDTO = modelMapper.map(post, PostDTO.class);
        return postDTO;
    }
}
