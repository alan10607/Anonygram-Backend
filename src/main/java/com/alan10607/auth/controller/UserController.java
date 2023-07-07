package com.alan10607.auth.controller;

import com.alan10607.auth.service.UserService;
import com.alan10607.auth.constant.RoleType;
import com.alan10607.auth.dto.UserDTO;
import com.alan10607.leaf.util.ResponseUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/user")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final ResponseUtil responseUtil;

    @GetMapping("/{userId}")
    public UserDTO findUser(@PathVariable("userId") String userId){
        return userService.findUser(userId);
    }

    @PostMapping()
    public void createUser(@RequestBody @Valid UserDTO userDTO){
        userService.createUser(userDTO, RoleType.NORMAL);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") String userId){
        userService.deleteUser(userId);
    }

}