package com.kimsreviews.API.Services;

import com.kimsreviews.API.DTO.PostDTO;
import com.kimsreviews.API.DTO.UserDTO;
import com.kimsreviews.API.Implementations.PostNotFoundException;
import com.kimsreviews.API.Implementations.UnauthorizedException;
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
public class PostServiceImpl {

    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepo userRepo;

    public List<PostDTO> getAllPosts() {
        return postRepo.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PostDTO getPostById(Long id) {
        return postRepo.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + id));
    }

    public PostDTO createPost(PostDTO postDTO, MultipartFile image) throws IOException {
        Post post = convertToEntity(postDTO);

        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            post.setImageUrl(imageUrl);
        }

        post.setCreatedAt(LocalDateTime.now());

        User user = fetchUserById(postDTO.getUserDTO().getId());
        if (user != null) {
            post.setUser(user);
        } else {
            throw new IllegalArgumentException("User not found with ID: " + postDTO.getUserDTO().getId());
        }

        Post savedPost = postRepo.save(post);
        return convertToDTO(savedPost);
    }

    public PostDTO updatePost(Long id, PostDTO postDTO, MultipartFile image) throws IOException {
        Post existingPost = postRepo.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + id));

        // Update the post fields except user details
        updatePostFields(existingPost, postDTO);

        // Handle image update if a new image is provided
        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            existingPost.setImageUrl(imageUrl);
        }

        // Preserve the original user details
        existingPost.setUser(existingPost.getUser());

        Post updatedPost = postRepo.save(existingPost);
        return convertToDTO(updatedPost);
    }


    public void deletePost(Long postId, Long userId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

//        User user = post.getUser();
//        if (user != null && user.getId()==userId.intValue()) {
            postRepo.deleteById(postId);
//        } else {
//            throw new UnauthorizedException("You are not authorized to delete this post.");
//        }
    }


    public void incrementLikes(Long postId, Long userId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (!post.getLikedBy().contains(user)) {
            post.getLikedBy().add(user);
            post.setLikes(post.getLikes() + 1);
            postRepo.save(post);
        }
    }

    public void incrementViews(Long id) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + id));

        post.setViews((post.getViews() == null ? 0 : post.getViews()) + 1);
        postRepo.save(post);
    }

    private String saveImage(MultipartFile image) throws IOException {
        String filename = image.getOriginalFilename();
        Path filepath = Paths.get(UPLOAD_DIR, filename);
        Files.createDirectories(filepath.getParent());
        Files.write(filepath, image.getBytes());
        return "/uploads/" + filename;
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

        User user = post.getUser();
        if (user != null) {
            dto.setUserDTO(userMapper.toDTO(user));
        } else {
            dto.setUserDTO(null);
            System.out.println("User is null for the post id: " + post.getId());
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
        String imageUrl = dto.getImageUrl();
        if (imageUrl != null) {
            post.setImageUrl(imageUrl.replace("\\", "/"));
        } else {
            post.setImageUrl(null);
        }
        post.setCreatedBy(dto.getCreatedBy());
        post.setLikes(dto.getLikes() == null ? 0 : dto.getLikes());
        post.setViews(dto.getViews() == null ? 0 : dto.getViews());
        post.setCreatedAt(dto.getCreatedAt());

        UserDTO userDTO = dto.getUserDTO();
        if (userDTO != null) {
            post.setUser(userMapper.toEntity(userDTO));
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

        UserDTO userDTO = postDTO.getUserDTO();
        if (userDTO != null) {
            existingPost.setUser(userMapper.toEntity(userDTO));
        }
    }

    private User fetchUserById(Long userId) {
        Optional<User> optionalUser = userRepo.findById(userId);
        return optionalUser.orElse(null);
    }
}
