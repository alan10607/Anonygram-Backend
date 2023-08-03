package com.alan10607.ag.controller.auth;

import com.alan10607.ag.dto.UserDTO;
import com.alan10607.ag.model.ForumUser;
import com.alan10607.ag.service.auth.AuthService;
import com.alan10607.ag.service.auth.JwtService;
import com.alan10607.ag.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping(path = "/auth")
@AllArgsConstructor
@Tag(name = "Login Authorization")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @GetMapping("/test")
    @Operation(summary = "Check login authorization")
    public UserDTO test(){
        ForumUser user = AuthUtil.getUser();
        return new UserDTO(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getUpdatedDate());
    }

    @PostMapping("/login")
    @Operation(summary = "To login system")
    public UserDTO login(@RequestBody @Valid UserDTO userDTO, HttpServletResponse response){
        return authService.login(userDTO, response);
    }

    @PostMapping("/anonymous")
    @Operation(summary = "To login as anonymous user")
    public UserDTO loginAnonymity(HttpServletResponse response){
        return authService.loginAnonymity(response);
    }

    @PostMapping("/register")
    @Operation(summary = "To register system")
    public void register(@RequestBody @Validated(UserDTO.registerGroup.class) UserDTO userDTO){
        authService.register(userDTO);
    }

}