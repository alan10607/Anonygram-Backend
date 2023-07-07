package com.alan10607.auth.controller;

import com.alan10607.auth.dto.UserDTO;
import com.alan10607.auth.service.AuthService;
import com.alan10607.leaf.util.ResponseUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final ResponseUtil responseUtil;

    @PostMapping("/login")
    public UserDTO login(@RequestBody @Validated({ UserDTO.LoginGroup.class }) UserDTO userDTO){
        return authService.login(userDTO);
    }

    @PostMapping("/anonymous")
    public UserDTO loginAnonymity(){
        return authService.loginAnonymity();
    }

    @PostMapping("/register")
    public void register(@RequestBody @Validated UserDTO userDTO){
        authService.register(userDTO);
    }

}