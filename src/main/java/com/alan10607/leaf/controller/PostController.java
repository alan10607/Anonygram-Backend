package com.alan10607.leaf.controller;

import com.alan10607.leaf.constant.AutoUserId;
import com.alan10607.leaf.dto.PostDTO;
import com.alan10607.leaf.service.PostService;
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
@RequestMapping(path = "/post")
@AllArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
    private final ResponseUtil responseUtil;

    @PostMapping("/uploadImg")
    @AutoUserId
    public ResponseEntity uploadImg(@RequestBody PostDTO postDTO){
        try{
            postDTO = postService.uploadImg(postDTO);
            return responseUtil.ok(postDTO);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

}