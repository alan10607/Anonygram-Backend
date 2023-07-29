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
    private String userName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String pw;
    private List<Role> userRole;
    private LocalDateTime updatedDate;
    private String token;

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


    public interface registerGroup extends Default {
    }
}