package com.kimsreviews.API.DTO;

import com.kimsreviews.API.models.User;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String password;
    private boolean above18;
    private String userImageUrl;
    private String createdBy;
    private List<String> documentUrls; // Assuming documentUrls is a list of strings

    public void setDocumentUrls(List<String> documentUrls) {
        this.documentUrls = documentUrls;
    }

    public List<String> getDocumentUrls() {
        return documentUrls;
    }

    public UserDTO() {}

    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.above18 = user.isAbove18();
        this.userImageUrl = user.getUserImageUrl();
        this.createdBy=user.getCreatedBy();
    }

    // Other methods as needed
}
