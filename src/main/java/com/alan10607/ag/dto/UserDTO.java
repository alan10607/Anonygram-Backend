package com.alan10607.ag.dto;

import com.alan10607.ag.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.groups.Default;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String id;

    @NotBlank(groups = registerGroup.class)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
    private List<Role> userRole;
    private LocalDateTime updatedDate;

    public UserDTO(String id,
                   String username,
                   String email,
                   List<Role> userRole,
                   LocalDateTime updatedDate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.userRole = userRole;
        this.updatedDate = updatedDate;
    }


    public interface registerGroup extends Default {
    }
}