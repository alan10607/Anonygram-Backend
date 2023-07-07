package com.alan10607.auth.controller;

import com.alan10607.auth.dto.UserDTO;
import com.alan10607.auth.model.ForumUser;
import com.alan10607.auth.service.AuthService;
import com.alan10607.auth.util.AuthUtil;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/test")
    public UserDTO test(){
        try {
            ForumUser user = AuthUtil.getUser();
            return new UserDTO(user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    user.getUpdatedDate());
        }catch (Exception e){
            throw new RuntimeException("Token invalid or expired");
        }
    }

    @PostMapping("/login")
    public UserDTO login(@RequestBody @Valid UserDTO userDTO){
        return authService.login(userDTO);
    }

    @PostMapping("/anonymous")
    public UserDTO loginAnonymity(){
        return authService.loginAnonymity();
    }

    @PostMapping("/register")
    public void register(@RequestBody @Validated({ UserDTO.registerGroup.class }) UserDTO userDTO){
        authService.register(userDTO);
    }

}