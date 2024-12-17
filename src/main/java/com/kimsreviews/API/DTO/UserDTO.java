package com.kimsreviews.API.DTO;
import com.kimsreviews.API.models.User;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;


@Data
public class UserDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String phoneNumber;
    private String email;
    private String password;
    private boolean above18;
    private String userImageUrl;
    private String createdBy;

    @Column(nullable = true)
    private List<String> documentUrls;



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
