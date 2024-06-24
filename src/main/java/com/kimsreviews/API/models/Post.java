package com.kimsreviews.API.models;

import com.kimsreviews.API.DTO.UserDTO;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Integer likes = 0;  // Initialize to 0
    private Integer views = 0;  // Initialize to 0

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}