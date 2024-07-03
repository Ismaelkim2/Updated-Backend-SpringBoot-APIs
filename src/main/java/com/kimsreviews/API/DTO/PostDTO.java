package com.kimsreviews.API.DTO;

import com.kimsreviews.API.models.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDTO {
    private Long id;
    private String title;
    private String productType;
    private Integer age;
    private Double salesAmount;
    private String poultryType;
    private Double weight;
    private String livestockType;
    private String livestockDescription;
    private String imageUrl;
    private String createdBy;
    private Integer likes;
    private Integer views;
    private UserDTO userDTO;
    private LocalDateTime createdAt;

    public PostDTO() {}

    public PostDTO(Post post, UserDTO userDTO) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.productType = post.getProductType();
        this.age = post.getAge();
        this.salesAmount = post.getSalesAmount();
        this.poultryType = post.getPoultryType();
        this.weight = post.getWeight();
        this.livestockType = post.getLivestockType();
        this.livestockDescription = post.getLivestockDescription();
        this.imageUrl = post.getImageUrl();
        this.createdBy = post.getCreatedBy();
        this.likes = post.getLikes();
        this.views = post.getViews();
        this.createdAt = post.getCreatedAt(); // Set createdAt from Post entity
        this.userDTO = userDTO;
    }

    public void setUser(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    public UserDTO getUser() {
        return userDTO;
    }


}
