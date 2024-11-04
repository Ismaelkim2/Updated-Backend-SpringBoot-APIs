package com.kimsreviews.API.models;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "workers")
public class Worker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String image;

    private String name;

    private String role;

    private Double salary;

    private String status;

    private String email;

    private String phone;
}
