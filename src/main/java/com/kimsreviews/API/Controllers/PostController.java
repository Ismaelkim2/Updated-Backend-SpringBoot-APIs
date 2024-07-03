package com.kimsreviews.API.Controllers;

import com.kimsreviews.API.DTO.PostDTO;
import com.kimsreviews.API.Repository.PostRepo;
import com.kimsreviews.API.Repository.UserRepo;
import com.kimsreviews.API.Services.PostService;
import com.kimsreviews.API.Services.UserMapper;
import com.kimsreviews.API.models.Post;
import com.kimsreviews.API.models.User;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    private PostService postService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public List<PostDTO> getPosts() {
        List<PostDTO> postDTOs = postService.getAllPosts();
        return postDTOs;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        PostDTO postDTO = postService.getPostById(id);
        if (postDTO != null) {
            return ResponseEntity.ok(postDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    public ResponseEntity<PostDTO> createPost(@RequestParam("title") String title,
                                              @RequestParam("productType") String productType,
                                              @RequestParam(value = "poultryType", required = false) String poultryType,
                                              @RequestParam(value = "weight", required = false) Double weight,
                                              @RequestParam(value = "livestockType", required = false) String livestockType,
                                              @RequestParam(value = "livestockDescription", required = false) String livestockDescription,
                                              @RequestParam(value = "age", required = false) Integer age,
                                              @RequestParam(value = "salesAmount", required = false) Double salesAmount,
                                              @RequestParam("image") MultipartFile image,
                                              @RequestParam("createdBy") String createdBy) throws IOException {
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

        PostDTO createdPostDTO = postService.createPost(postDTO, image);
        return ResponseEntity.ok(createdPostDTO);
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
                                              @RequestParam("createdBy") String createdBy) throws IOException {
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

        PostDTO updatedPostDTO = postService.updatePost(id, postDTO, image);
        if (updatedPostDTO != null) {
            return ResponseEntity.ok(updatedPostDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PostMapping("/{postId}/likes")
    public ResponseEntity<PostDTO> incrementLikes(
            @PathVariable Long postId,
            @RequestParam Long userId) {
        Optional<Post> optionalPost = postRepo.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            Optional<User> optionalUser = userRepo.findById(Math.toIntExact(userId));
            if (optionalUser.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            User user = optionalUser.get();

            // Check if the user has already liked the post
//            if (post.getLikedBy().contains(user)) {
//                return ResponseEntity.badRequest().body("User has already liked this post.");
//            }

            post.getLikedBy().add(user);
            post.setLikes(post.getLikes() + 1);

            Post updatedPost = postRepo.save(post);
            PostDTO postDTO = convertToDTO(updatedPost);
            return ResponseEntity.ok(postDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Other methods in your PostController

    private PostDTO convertToDTO(Post post) {
        // Implement your conversion logic here
        return null; // Replace with actual conversion logic
    }
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
}
