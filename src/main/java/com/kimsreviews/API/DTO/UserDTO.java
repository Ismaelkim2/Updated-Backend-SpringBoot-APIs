package com.kimsreviews.API.DTO;

import com.kimsreviews.API.models.User;
import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;


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
    private List<String> documentUrls;

    @Value("${imgur.client-id}")
    private String clientId;



//    private Set<String> roles;



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


}
