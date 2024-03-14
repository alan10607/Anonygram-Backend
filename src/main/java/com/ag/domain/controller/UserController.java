package com.ag.domain.controller;

import com.ag.domain.dto.UserDTO;
import com.ag.domain.model.ForumUser;
import com.ag.domain.service.UserService;
import com.ag.domain.util.PojoFiledUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Tag(name = "User")
@RequestMapping(path = "/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get a user")
    public UserDTO get(@PathVariable("userId") String userId) {
        return PojoFiledUtil.convertObject(userService.get(userId), UserDTO.class);
    }

    @PostMapping()
    @Operation(summary = "To register user")
    public UserDTO create(@RequestBody UserDTO userDTO) {
        ForumUser user = PojoFiledUtil.convertObject(userDTO, ForumUser.class);
        return PojoFiledUtil.convertObject(userService.create(user), UserDTO.class);
    }

    @PatchMapping("/{userId}")
    @Operation(summary = "To patch a user")
    public void patch(@PathVariable("userId") String userId,
                      @RequestBody UserDTO userDTO) {
        ForumUser user = PojoFiledUtil.convertObject(userDTO, ForumUser.class);
        user.setId(userId);
        userService.patch(user);
    }

}