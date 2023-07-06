package com.alan10607.leaf.controller;

import com.alan10607.auth.dto.UserDTO;
import com.alan10607.leaf.service.AuthService;
import com.alan10607.leaf.util.ResponseUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity login(@RequestBody UserDTO userDTO){
        try{
            userDTO = authService.login(userDTO.getEmail(), userDTO.getPw());
            return responseUtil.ok(userDTO);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/anony")
    public ResponseEntity anony(@RequestBody UserDTO userDTO){
        try{
            userDTO = authService.loginAnonymity();
            return responseUtil.ok(userDTO);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody UserDTO userDTO){
        try{
            authService.register(userDTO.getEmail(), userDTO.getUserName(), userDTO.getPw());
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

}