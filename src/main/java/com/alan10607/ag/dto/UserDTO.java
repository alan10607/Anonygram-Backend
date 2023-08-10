package com.alan10607.ag.dto;

import com.alan10607.ag.constant.LanguageType;
import com.alan10607.ag.constant.ThemeType;
import com.alan10607.ag.model.Role;
import com.alan10607.ag.util.ToolUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.groups.Default;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    private String headUrl;
    private LanguageType language;
    private ThemeType theme;
    private LocalDateTime updatedDate;

    public UserDTO(String id,
                   String username,
                   String headUrl) {
        this.id = id;
        this.username = username;
        this.headUrl = headUrl;
    }


    public UserDTO(String id,
                   String username,
                   String email,
                   List<Role> userRole,
                   String headUrl,
                   LanguageType language,
                   ThemeType theme,
                   LocalDateTime updatedDate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.userRole = userRole;
        this.headUrl = headUrl;
        this.language = language;
        this.theme = theme;
        this.updatedDate = updatedDate;
    }

    public interface registerGroup extends Default {
    }

    public static UserDTO toDTO(Object data) {
        return ToolUtil.convertValue(data, UserDTO.class);
    }

    public Map<String, Object> toMap() {
        return ToolUtil.convertValue(this, Map.class);
    }


}