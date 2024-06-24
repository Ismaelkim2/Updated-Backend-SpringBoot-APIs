package com.kimsreviews.API.Services;

import com.kimsreviews.API.models.Post;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostServic {
    List<Post> getAllPosts();

    Post getPostById(Long id);

    Post createPost(Post post);

    Post updatePost(Long id, Post post);

    Post createPost(Post post, MultipartFile image) throws IOException;

    Post updatePost(Long id, Post postDetails, MultipartFile image) throws IOException;

    void deletePost(Long id);

    void incrementLikes(Long id);

    void incrementViews(Long id);
}
