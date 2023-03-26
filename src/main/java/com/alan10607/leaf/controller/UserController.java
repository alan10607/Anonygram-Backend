package com.alan10607.leaf.controller;

import com.alan10607.leaf.constant.LeafRoleType;
import com.alan10607.leaf.dto.LeafUserDTO;
import com.alan10607.leaf.service.UserService;
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

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LeafUserDTO leafUserDTO){
        try{
            leafUserDTO = userService.login(leafUserDTO.getEmail(), leafUserDTO.getPw());
            return responseUtil.ok(leafUserDTO);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/loginAnonymity")
    public ResponseEntity loginAnonymity(@RequestBody LeafUserDTO leafUserDTO){
        try{
            leafUserDTO = userService.loginAnonymity();
            return responseUtil.ok(leafUserDTO);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody LeafUserDTO leafUserDTO){
        try{
            userService.register(leafUserDTO.getEmail(), leafUserDTO.getUserName(), leafUserDTO.getPw());
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/findUser")
    public ResponseEntity findUser(@RequestBody LeafUserDTO leafUserDTO){
        try{
            leafUserDTO = userService.findUser(leafUserDTO.getEmail());
            return responseUtil.ok(leafUserDTO);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/findAllUser")
    public ResponseEntity findAllUser(@RequestBody LeafUserDTO leafUserDTO){
        try{
            List<LeafUserDTO> leafUserDTOList = userService.findAllUser();
            return responseUtil.ok(leafUserDTOList);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/createUser")
    public ResponseEntity createUser(@RequestBody LeafUserDTO leafUserDTO){
        try{
            userService.createUser(leafUserDTO.getEmail(),
                    leafUserDTO.getUserName(),
                    leafUserDTO.getPw(),
                    LeafRoleType.NORMAL
            );
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/deleteUser")
    public ResponseEntity deleteUser(@RequestBody LeafUserDTO leafUserDTO){
        try{
            userService.deleteUser(leafUserDTO.getEmail());
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

}