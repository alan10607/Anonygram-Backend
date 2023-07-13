package com.alan10607.auth.controller;

import com.alan10607.auth.constant.RoleType;
import com.alan10607.auth.dto.UserDTO;
import com.alan10607.auth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{email}")
    public UserDTO findUser(@PathVariable("email") String email){
        return userService.findUser(email);
    }

    @PostMapping()
    public void createUser(@RequestBody @Valid UserDTO userDTO){
        userService.createUser(userDTO, RoleType.NORMAL);
    }

    @DeleteMapping("/{email}")
    public void deleteUser(@PathVariable("email") String email){
        userService.deleteUser(email);
    }

}