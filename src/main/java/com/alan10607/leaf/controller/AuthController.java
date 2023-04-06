package com.alan10607.leaf.controller;

import com.alan10607.leaf.dto.LeafUserDTO;
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
    public ResponseEntity login(@RequestBody LeafUserDTO leafUserDTO){
        try{
            leafUserDTO = authService.login(leafUserDTO.getEmail(), leafUserDTO.getPw());
            return responseUtil.ok(leafUserDTO);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/anony")
    public ResponseEntity anony(@RequestBody LeafUserDTO leafUserDTO){
        try{
            leafUserDTO = authService.loginAnonymity();
            return responseUtil.ok(leafUserDTO);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody LeafUserDTO leafUserDTO){
        try{
            authService.register(leafUserDTO.getEmail(), leafUserDTO.getUserName(), leafUserDTO.getPw());
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

}