package com.alan10607.ag.controller.auth;

import com.alan10607.ag.dto.UserDTO;
import com.alan10607.ag.service.auth.UserService;
import com.alan10607.ag.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping()
    @Operation(summary = "Update for user preferences")
    public UserDTO update(@RequestBody UserDTO userDTO){
        userDTO.setId(AuthUtil.getUserId());
        userService.update(userDTO);
        return userDTO;
    }

}