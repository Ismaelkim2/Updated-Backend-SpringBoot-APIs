package com.kimsreviews.API.DTO;

import com.kimsreviews.API.models.User;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String password;
    private boolean above18;
    private String userImageUrl;
    private String createdBy;
    private boolean isVerified;
    private List<String> documentUrls;
//    private Set<String> roles;


    public void setDocumentUrls(List<String> documentUrls) {
        this.documentUrls = documentUrls;
    }

    public List<String> getDocumentUrls() {
        return documentUrls;
    }

    public UserDTO() {}

    public UserDTO(User user) {
        this.id = (long) user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.above18 = user.isAbove18();
        this.userImageUrl = user.getUserImageUrl();
        this.createdBy=user.getCreatedBy();
    }

    public void setIsVerified(boolean b) {
    }
}
