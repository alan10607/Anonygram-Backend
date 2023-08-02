package com.alan10607.ag.controller.forum;

import com.alan10607.ag.dto.ContentDTO;
import com.alan10607.ag.dto.ForumDTO;
import com.alan10607.ag.dto.LikeDTO;
import com.alan10607.ag.service.forum.ForumService;
import com.alan10607.ag.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/forum")
@AllArgsConstructor
@Tag(name = "Anonygram Forum")
public class ForumController {
    private final ForumService forumService;

    @GetMapping("/id")
    @Operation(summary = "Get all ids of article")
    public List<String> getId(){
        return forumService.getId();
    }

    @GetMapping("/article/{id}")
    @Operation(summary = "Get a article with the original poster content")
    public List<ForumDTO> getFirstForums(@PathVariable("id") List<String> idList){
        return forumService.getFirstForums(idList);
    }

    @PostMapping("/article")
    @Operation(summary = "Create a article with the original poster content")
    public ForumDTO createForum(@RequestBody @Validated(ForumDTO.CreateForumGroup.class) ForumDTO forumDTO){
        forumDTO.setAuthor(AuthUtil.getUserId());
        return forumService.createForum(forumDTO);
    }

    @DeleteMapping("/article/{id}")
    @Operation(summary = "Delete a article")
    public void deleteContent(@PathVariable("id") String id){
        forumService.deleteForum(id, AuthUtil.getUserId());
    }

    @GetMapping("/content/{id}/{no}")
    @Operation(summary = "Get top 10 of the content")
    public List<ContentDTO> getTopContents(@PathVariable("id") String id,
                                           @PathVariable("no") int no){
        return forumService.getTopContents(id, no, 10);
    }

    @PostMapping("/content/{id}")
    @Operation(summary = "Create a content to reply the article")
    public ContentDTO replyForum(@PathVariable("id") String id,
                                 @RequestBody @Validated(ForumDTO.ReplyForumGroup.class) ForumDTO forumDTO){//TODO: NEED TEST
        forumDTO.setId(id);
        forumDTO.setAuthor(AuthUtil.getUserId());
        forumDTO = forumService.replyForum(forumDTO);
        List<ContentDTO> requeryContent = forumService.getTopContents(forumDTO.getId(), forumDTO.getNo(), 1);
        return requeryContent.get(0);
    }

    @DeleteMapping("/content/{id}/{no}")
    @Operation(summary = "Delete a content")
    public void deleteContent(@PathVariable("id") String id,
                              @PathVariable("no") int no){
        forumService.deleteContent(id, no, AuthUtil.getUserId());
    }

    @PatchMapping("/like/{id}/{no}")
    @Operation(summary = "To like a content")
    public boolean likeContent(@PathVariable("id") String id,
                               @PathVariable("no") int no,
                               @RequestBody @Validated(ForumDTO.LikeContentGroup.class) ForumDTO forumDTO){
        LikeDTO likeDTO = new LikeDTO(id, no, AuthUtil.getUserId(), forumDTO.getLike());
        return forumService.likeOrDislikeContent(likeDTO);
    }

    @PostMapping("/img")
    @Operation(summary = "Upload a image in base64 format")
    public ForumDTO uploadImg(@RequestBody @Validated(ForumDTO.UploadImgGroup.class) ForumDTO forumDTO){
        forumDTO.setAuthor(AuthUtil.getUserId());
        return forumService.upload(forumDTO);
    }

}