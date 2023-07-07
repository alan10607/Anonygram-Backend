package com.alan10607.leaf.controller;

import com.alan10607.leaf.service.LikeService;
import com.alan10607.redis.dto.ContentDTO;
import com.alan10607.leaf.dto.ForumDTO;
import com.alan10607.redis.dto.LikeDTO;
import com.alan10607.leaf.service.ForumService;
import com.alan10607.auth.util.AuthUtil;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/forum")
@AllArgsConstructor
public class ForumController {
    private final ForumService forumService;
    private final LikeService likeService;

    @GetMapping("/id")
    public List<String> getId(){
        return forumService.getId();
    }

    @GetMapping("/article/{id}")
    public List<ForumDTO> getFirstForums(@PathVariable("id") List<String> idList){
        return forumService.getFirstForums(idList);
    }

    @PostMapping("/article")
    public ForumDTO createForum(@RequestBody @Validated({ ForumDTO.CreateForumGroup.class }) ForumDTO forumDTO){
        forumDTO.setAuthor(AuthUtil.getUserId());
        return forumService.createForum(forumDTO);
    }

    @DeleteMapping("/article/{id}")
    public void deleteContent(@PathVariable("id") String id){
        forumService.deleteForum(id, AuthUtil.getUserId());
    }

    @GetMapping("/content/{id}/{no}")
    public List<ContentDTO> getTopContents(@PathVariable("id") String id,
                                           @PathVariable("no") int no){
        return forumService.getTopContents(id, no);
    }

    @PostMapping("/content/{id}")
    public ContentDTO replyForum(@PathVariable("id") String id,
                                 @RequestBody @Validated ForumDTO forumDTO){
        forumDTO.setId(id);
        forumDTO.setAuthor(AuthUtil.getUserId());
        forumDTO = forumService.replyForum(forumDTO);
        List<ContentDTO> requeryContent = forumService.getTopContents(forumDTO.getId(), forumDTO.getNo(), 1);
        return requeryContent.get(0);
    }

    @DeleteMapping("/content/{id}/{no}")
    public void deleteContent(@PathVariable("id") String id,
                              @PathVariable("no") int no){
        forumService.deleteContent(id, no, AuthUtil.getUserId());
    }

    @PatchMapping("/like/{id}/{no}")
    public boolean likeContent(@PathVariable("id") String id,
                               @PathVariable("no") int no){
        LikeDTO likeDTO = new LikeDTO(id, no, AuthUtil.getUserId(), true);
        return forumService.likeOrDislikeContent(likeDTO);
    }

    @PatchMapping("/dislike/{id}/{no}")
    public boolean dislikeContent(@PathVariable("id") String id,
                                  @PathVariable("no") int no){
        LikeDTO likeDTO = new LikeDTO(id, no, AuthUtil.getUserId(), false);
        return forumService.likeOrDislikeContent(likeDTO);
    }

    @PostMapping("/saveLike")
    public int saveLikeToDB(){
        return likeService.saveLikeToDB();
    }


}