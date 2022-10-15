package com.alan10607.leaf.controller;

import com.alan10607.leaf.constant.AutoUserId;
import com.alan10607.leaf.dto.LeafDTO;
import com.alan10607.leaf.dto.PostDTO;
import com.alan10607.leaf.service.ContLikeService;
import com.alan10607.leaf.service.ImgurService;
import com.alan10607.leaf.service.PostService;
import com.alan10607.leaf.util.ResponseUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(path = "/post")
@AllArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
    private final ImgurService imgurService;
    private final ResponseUtil responseUtil;


    private final ContLikeService contLikeService;

    @PostMapping("/findIdSet")
    public ResponseEntity findIdSet(@RequestBody PostDTO postDTO){
        try{
            List<String> idList = postService.findIdSet();
            return responseUtil.ok(idList);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/findPost")
    @AutoUserId
    public ResponseEntity findPost(@RequestBody PostDTO postDTO){
        try{
            List<PostDTO> postList = postService.findPost(postDTO);
            return responseUtil.ok(postList);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/findTopCont")
    @AutoUserId
    public ResponseEntity findTopCont(@RequestBody PostDTO postDTO){
        try{
            List<PostDTO> contList = postService.findTopCont(postDTO);
            return responseUtil.ok(contList);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/findBotCont")
    @AutoUserId
    public ResponseEntity findBotCont(@RequestBody PostDTO postDTO){
        try{
            List<PostDTO> contList = postService.findBotCont(postDTO);
            return responseUtil.ok(contList);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/createPost")
    @AutoUserId
    public ResponseEntity createPost(@RequestBody PostDTO postDTO){
        try{
            postService.createPost(postDTO);
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/image")
    @AutoUserId
    //可能要用RequestParam了...
    public ResponseEntity uploadFileMulti(@RequestParam("image") MultipartFile image) {
        try{
            imgurService.upload(image);
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }

    }

    @PostMapping("/replyPost")
    @AutoUserId
    public ResponseEntity replyPost(@RequestBody PostDTO postDTO){
        try{
            postService.replyPost(postDTO);
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/deletePost")
    @AutoUserId
    public ResponseEntity deletePost(@RequestBody PostDTO postDTO){
        try{
            postService.deletePost(postDTO);
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/deleteContent")
    @AutoUserId
    public ResponseEntity deleteContent(@RequestBody PostDTO postDTO){
        try{
            postService.deleteContent(postDTO);
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/likeContent")
    @AutoUserId
    public ResponseEntity likeContent(@RequestBody PostDTO postDTO){
        try{
            postDTO = postService.likeContent(postDTO);
            return responseUtil.ok(postDTO);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/unlikeContent")
    @AutoUserId
    public ResponseEntity unlikeContent(@RequestBody PostDTO postDTO){
        try{
            postDTO = postService.unlikeContent(postDTO);
            return responseUtil.ok(postDTO);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }


    @PostMapping("/t")
    @AutoUserId
    public ResponseEntity t(@RequestBody LeafDTO leafDTO){
        try{
            contLikeService.saveContLikeToDB();
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

}