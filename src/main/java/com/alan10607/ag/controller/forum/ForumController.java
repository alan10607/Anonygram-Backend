package com.alan10607.ag.controller.forum;

import com.alan10607.ag.dto.ContentDTO;
import com.alan10607.ag.dto.ForumDTO;
import com.alan10607.ag.dto.LikeDTO;
import com.alan10607.ag.exception.AnonygramIllegalStateException;
import com.alan10607.ag.service.forum.ForumService;
import com.alan10607.ag.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.validation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
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

    @GetMapping("/articles/{idList}")
    @Operation(summary = "Get a article with the original poster content")
    public List<ForumDTO> getFirstForums(@PathVariable("idList") List<String> idList){
        validListSize(idList, 0, 10);
        return forumService.getArticles(idList);
    }




    @PostMapping("/article")
    @Operation(summary = "Create a article with the original poster content")
    public ForumDTO createForum(@RequestBody @Validated(ForumDTO.CreateForumGroup.class) ForumDTO forumDTO){
        forumDTO.setAuthor(AuthUtil.getUserId());
        return forumService.createArticle(forumDTO);
    }

    @DeleteMapping("/article/{id}")
    @Operation(summary = "Delete a article")
    public void deleteContent(@PathVariable("id") String id){
        forumService.deleteArticle(id, AuthUtil.getUserId());
    }

    @GetMapping("/contents/{id}/{noList}")
    @Operation(summary = "Get top 10 of the content")
    public List<ContentDTO> getTopContents(@PathVariable("id") String id,
                                           @PathVariable("noList") List<Integer> noList){
        validListSize(noList, 0, 10);
        return forumService.getContents(id, noList);
    }

    @PostMapping("/content/{id}")
    @Operation(summary = "Create a content to reply the article")
    public ContentDTO replyForum(@PathVariable("id") String id,
                                 @RequestBody @Validated(ForumDTO.ReplyForumGroup.class) ForumDTO forumDTO){
        forumDTO.setId(id);
        forumDTO.setAuthor(AuthUtil.getUserId());
        forumDTO = forumService.createContent(forumDTO);
        return forumService.getContent(forumDTO.getId(), forumDTO.getNo());
    }

    @DeleteMapping("/content/{id}/{no}")
    @Operation(summary = "Delete a content")
    public void deleteContent(@PathVariable("id") String id,
                              @PathVariable("no") int no){
        forumService.deleteContent(id, no, AuthUtil.getUserId());
    }

    @PatchMapping("/like/{id}/{no}")
    @Operation(summary = "To like a content")
    public LikeDTO likeContent(@PathVariable("id") String id,
                               @PathVariable("no") int no,
                               @RequestBody @Validated(ForumDTO.LikeContentGroup.class) ForumDTO forumDTO){
        LikeDTO likeDTO = new LikeDTO(id, no, AuthUtil.getUserId(), forumDTO.getLike());
        forumService.likeOrDislikeContent(likeDTO);
        return likeDTO;
    }

    @PostMapping("/img")
    @Operation(summary = "Upload a image in base64 format")
    public ForumDTO uploadImg(@RequestBody @Validated(ForumDTO.UploadImgGroup.class) ForumDTO forumDTO){
        forumDTO.setAuthor(AuthUtil.getUserId());
        return forumService.upload(forumDTO);
    }

    private <T> void validListSize(List<T> list, int min, int max){
        if(list.size() < min || list.size() > max){
            throw new AnonygramIllegalStateException(String.format("Path variable list size must be in %s ~ %s", min, max));
        }
    }

}