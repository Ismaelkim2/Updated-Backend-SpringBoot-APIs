package com.kimsreviews.API.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
    private String email;
    private String userImageUrl;
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;
    private boolean above18;




    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles = new HashSet<>();

    private String createdBy;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;

    private String resetToken;

    // Optional getters and setters (if you want custom behavior)
    public void setDocumentUrls(List<String> documentUrls) {
        // Placeholder for document URLs, if needed
    }

    public List<String> getDocumentUrls() {
        return null; // Placeholder for future implementation
    }


}
