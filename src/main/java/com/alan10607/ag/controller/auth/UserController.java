package com.alan10607.ag.controller.auth;

import com.alan10607.ag.constant.RoleType;
import com.alan10607.ag.dto.UserDTO;
import com.alan10607.ag.service.auth.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get a user")
    public UserDTO get(@PathVariable("userId") String userId){
        return userService.getUser(userId);
    }

    @PatchMapping("/{userId}/language")
    @Operation(summary = "Update for user preference language")
    public void updateLanguage(UserDTO userDTO) {
        userService.updateLanguage(userDTO.getLanguage());
    }

    @PatchMapping("/{userId}/theme")
    @Operation(summary = "Update for user preference theme")
    public void updateTheme(UserDTO userDTO) {
        userService.updateTheme(userDTO.getTheme());
    }
}