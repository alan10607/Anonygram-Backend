package com.alan10607.ag.controller.auth;

import com.alan10607.ag.dto.ImageDTO;
import com.alan10607.ag.dto.UserDTO;
import com.alan10607.ag.service.auth.UserService;
import com.alan10607.ag.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping()
    @Operation(summary = "Get a user in open data")
    public UserDTO get(){
        return userService.getUser(AuthUtil.getUserId());
    }

    @PatchMapping("/headUrl")
    @Operation(summary = "Update for user head url")
    public void update(@RequestBody @Validated ImageDTO imageDTO){
        imageDTO.setScope("headUrl");
        imageDTO.setUserId(AuthUtil.getUserId());
        userService.updateHeadUrl(imageDTO);
    }

    @PatchMapping()
    @Operation(summary = "Update for user preference")
    public void update(@RequestBody UserDTO userDTO){
        userDTO.setId(AuthUtil.getUserId());
        userService.update(userDTO);
    }

}