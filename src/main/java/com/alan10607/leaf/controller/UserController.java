package com.alan10607.leaf.controller;

import com.alan10607.auth.service.UserService;
import com.alan10607.auth.constant.RoleType;
import com.alan10607.auth.dto.UserDTO;
import com.alan10607.leaf.util.ResponseUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/user")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final ResponseUtil responseUtil;

    @PostMapping("/findUser")
    public ResponseEntity findUser(@RequestBody UserDTO userDTO){
        try{
            userDTO = userService.findUser(userDTO.getEmail());
            return responseUtil.ok(userDTO);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/findAllUser")
    public ResponseEntity findAllUser(@RequestBody UserDTO userDTO){
        try{
            List<UserDTO> userDTOList = userService.findAllUser();
            return responseUtil.ok(userDTOList);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/createUser")
    public ResponseEntity createUser(@RequestBody UserDTO userDTO){
        try{
            userService.createUser(userDTO.getEmail(),
                    userDTO.getUserName(),
                    userDTO.getPw(),
                    RoleType.NORMAL
            );
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/deleteUser")
    public ResponseEntity deleteUser(@RequestBody UserDTO userDTO){
        try{
            userService.deleteUser(userDTO.getEmail());
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

}