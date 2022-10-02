package com.alan10607.leaf.controller;

import com.alan10607.leaf.dto.LeafDTO;
import com.alan10607.leaf.dto.PostDTO;
import com.alan10607.leaf.service.ContLikeService;
import com.alan10607.leaf.service.PostService;
import com.alan10607.leaf.util.ResponseUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping(path = "/post")
@AllArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
    private final ResponseUtil responseUtil;


    private final ContLikeService contLikeService;

    @PostMapping("/findArtSet")
    public ResponseEntity findArtSet(@RequestBody PostDTO postDTO, HttpSession session){
        try{
            session.getId();//寫成AOP??
            List<String> idList = postService.findArtSet();
            return responseUtil.ok(idList);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/findPosts")
    public ResponseEntity findPosts(@RequestBody PostDTO postDTO){
        try{
            List<PostDTO> postList = postService.findPosts(postDTO);
            return responseUtil.ok(postList);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/openTop")
    public ResponseEntity openTop(@RequestBody PostDTO postDTO){
        try{
            List<PostDTO> contList = postService.openTop(postDTO);
            return responseUtil.ok(contList);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/openBot")
    public ResponseEntity openBot(@RequestBody PostDTO postDTO){
        try{
            List<PostDTO> contList = postService.openTop(postDTO);
            return responseUtil.ok(contList);
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/createPost")
    public ResponseEntity createPost(@RequestBody PostDTO postDTO){
        try{
            postService.createPost(postDTO);
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/replyPost")
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
    public ResponseEntity likeContent(@RequestBody PostDTO postDTO){
        try{
            postService.likeContent(postDTO);
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/unlikeContent")
    public ResponseEntity unlikeContent(@RequestBody PostDTO postDTO){
        try{
            postService.unlikeContent(postDTO);
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }


    @PostMapping("/t")
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