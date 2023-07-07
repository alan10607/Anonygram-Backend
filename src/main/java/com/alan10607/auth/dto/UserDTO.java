package com.alan10607.auth.dto;

import com.alan10607.auth.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.groups.Default;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Data
@NoArgsConstructor
public class UserDTO {

    private String id;
    @NotBlank
    private String userName;

    @NotBlank(groups = LoginGroup.class)
    private String email;

    @NotBlank(groups = LoginGroup.class)
    private String pw;
    private List<Role> userRole;
    private LocalDateTime updatedDate;
    private String token;
    private boolean isAnonymousId;

    public UserDTO(String id,
                   String userName,
                   String email,
                   List<Role> userRole,
                   LocalDateTime updatedDate) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.userRole = userRole;
        this.updatedDate = updatedDate;
    }


    public interface LoginGroup extends Default {
    }
}