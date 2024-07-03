package com.kimsreviews.API.Services;

import com.kimsreviews.API.DTO.PostDTO;
import com.kimsreviews.API.DTO.UserDTO;
import com.kimsreviews.API.Repository.PostRepo;
import com.kimsreviews.API.Repository.UserRepo;
import com.kimsreviews.API.models.Post;
import com.kimsreviews.API.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserServiceImpl userServiceImpl;

     @Autowired
     private UserRepo userRepo;

    public List<PostDTO> getAllPosts() {
        return postRepo.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PostDTO getPostById(Long id) {
        Optional<Post> optionalPost = postRepo.findById(id);
        return optionalPost.map(this::convertToDTO).orElse(null);
    }

    public PostDTO createPost(PostDTO postDTO) {
        Post post = convertToEntity(postDTO);
        Post savedPost = postRepo.save(post);
        return convertToDTO(savedPost);
    }

    public PostDTO updatePost(Long id, PostDTO postDTO) {
        Optional<Post> optionalExistingPost = postRepo.findById(id);
        if (optionalExistingPost.isPresent()) {
            Post existingPost = optionalExistingPost.get();
            updatePostFields(existingPost, postDTO);
            Post updatedPost = postRepo.save(existingPost);
            return convertToDTO(updatedPost);
        }
        return null;
    }

    public PostDTO createPost(PostDTO postDTO, MultipartFile image) throws IOException {
        Post post = convertToEntity(postDTO);
        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            post.setImageUrl(imageUrl);
        }
        post.setCreatedAt(LocalDateTime.now()); // Set the createdAt field
        Post savedPost = postRepo.save(post);
        return convertToDTO(savedPost);
    }

    public PostDTO updatePost(Long id, PostDTO postDTO, MultipartFile image) throws IOException {
        Optional<Post> optionalExistingPost = postRepo.findById(id);
        if (optionalExistingPost.isPresent()) {
            Post existingPost = optionalExistingPost.get();
            updatePostFields(existingPost, postDTO);
            if (image != null && !image.isEmpty()) {
                String imageUrl = saveImage(image);
                existingPost.setImageUrl(imageUrl);
            }
            Post updatedPost = postRepo.save(existingPost);
            return convertToDTO(updatedPost);
        }
        return null;
    }

    public void deletePost(Long id) {
        postRepo.deleteById(id);
    }

    public void incrementLikes(Long postId, Long userId) {
        Optional<Post> optionalPost = postRepo.findById(postId);
        Optional<User> optionalUser = userRepo.findById(Math.toIntExact(userId));

        if (optionalPost.isPresent() && optionalUser.isPresent()) {
            Post post = optionalPost.get();
            User user = optionalUser.get();

            if (!post.getLikedBy().contains(user)) {
                post.getLikedBy().add(user);
                post.setLikes(post.getLikes() + 1);
                postRepo.save(post);
            }
        }
    }


    public void incrementViews(Long id) {
        Optional<Post> optionalPost = postRepo.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setViews((post.getViews() == null ? 0 : post.getViews()) + 1);
            postRepo.save(post);
        }
    }

    private String saveImage(MultipartFile image) throws IOException {
        String filename = image.getOriginalFilename();
        Path filepath = Paths.get(UPLOAD_DIR, filename);
        Files.createDirectories(filepath.getParent());
        Files.write(filepath, image.getBytes());
        return filepath.toString();
    }

    private PostDTO convertToDTO(Post post) {
        if (post == null) {
            return null;
        }
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setProductType(post.getProductType());
        dto.setAge(post.getAge());
        dto.setSalesAmount(post.getSalesAmount());
        dto.setPoultryType(post.getPoultryType());
        dto.setWeight(post.getWeight());
        dto.setLivestockType(post.getLivestockType());
        dto.setLivestockDescription(post.getLivestockDescription());
        dto.setImageUrl(post.getImageUrl());
        dto.setCreatedBy(post.getCreatedBy());
        dto.setLikes(post.getLikes());
        dto.setViews(post.getViews());
        dto.setCreatedAt(post.getCreatedAt());

        // Fetch user details and set in DTO
        User user = post.getUser();
        if (user != null) {
            dto.setUser(userMapper.toDTO(user)); // Ensure user is mapped to UserDTO
        }

        return dto;
    }

    private Post convertToEntity(PostDTO dto) {
        if (dto == null) {
            return null;
        }
        Post post = new Post();
        post.setId(dto.getId());
        post.setTitle(dto.getTitle());
        post.setProductType(dto.getProductType());
        post.setAge(dto.getAge());
        post.setSalesAmount(dto.getSalesAmount());
        post.setPoultryType(dto.getPoultryType());
        post.setWeight(dto.getWeight());
        post.setLivestockType(dto.getLivestockType());
        post.setLivestockDescription(dto.getLivestockDescription());
        post.setImageUrl(dto.getImageUrl());
        post.setCreatedBy(dto.getCreatedBy());
        post.setLikes(dto.getLikes() == null ? 0 : dto.getLikes());
        post.setViews(dto.getViews() == null ? 0 : dto.getViews());
        post.setCreatedAt(dto.getCreatedAt());
        UserDTO userDTO = dto.getUser();
        if (userDTO != null) {
            post.setUser(userMapper.toEntity(userDTO)); // Use dto.getUser() to get UserDTO
        }
        return post;
    }

    private void updatePostFields(Post existingPost, PostDTO postDTO) {
        if (existingPost == null || postDTO == null) {
            return;
        }
        existingPost.setTitle(postDTO.getTitle());
        existingPost.setProductType(postDTO.getProductType());
        existingPost.setAge(postDTO.getAge());
        existingPost.setSalesAmount(postDTO.getSalesAmount());
        existingPost.setPoultryType(postDTO.getPoultryType());
        existingPost.setWeight(postDTO.getWeight());
        existingPost.setLivestockType(postDTO.getLivestockType());
        existingPost.setLivestockDescription(postDTO.getLivestockDescription());
        existingPost.setCreatedBy(postDTO.getCreatedBy());
        existingPost.setLikes(postDTO.getLikes() == null ? 0 : postDTO.getLikes());
        existingPost.setViews(postDTO.getViews() == null ? 0 : postDTO.getViews());
        existingPost.setCreatedAt(postDTO.getCreatedAt());
    }
}
