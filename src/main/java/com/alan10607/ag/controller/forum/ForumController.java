package com.alan10607.ag.controller.forum;

import com.alan10607.ag.dto.ArticleDTO;
import com.alan10607.ag.dto.ContentDTO;
import com.alan10607.ag.dto.ForumDTO;
import com.alan10607.ag.exception.AnonygramIllegalStateException;
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
    @Operation(summary = "Get a article with the first content")
    public ArticleDTO getArticle(@PathVariable("id") String id){
        return forumService.getArticle(id);
    }

    @GetMapping("/articles/{idList}")
    @Operation(summary = "Get article list with the first content")
    public List<ArticleDTO> getArticles(@PathVariable("idList") List<String> idList){
        validListSize(idList, 0, 10);
        return forumService.getArticles(idList);
    }

    @PostMapping("/article")
    @Operation(summary = "Create a article with the original poster content")
    public ArticleDTO createArticle(@RequestBody @Validated(ForumDTO.CreateArticleGroup.class) ForumDTO forumDTO){
        forumDTO.setAuthorId(AuthUtil.getUserId());
        forumDTO = forumService.createArticle(forumDTO);
        return forumService.getArticle(forumDTO.getId());
    }

    @DeleteMapping("/article/{id}")
    @Operation(summary = "Delete a article")
    public void deleteArticle(@PathVariable("id") String id){
         forumService.deleteArticle(id, AuthUtil.getUserId());
    }

    @GetMapping("/content/{id}/{no}")
    @Operation(summary = "Get a content")
    public ContentDTO getContent(@PathVariable("id") String id,
                                 @PathVariable("no") int no){
        return forumService.getContent(id, no);
    }

    @GetMapping("/contents/{id}/{noList}")
    @Operation(summary = "Get contents list")
    public List<ContentDTO> getContents(@PathVariable("id") String id,
                                        @PathVariable("noList") List<Integer> noList){
        validListSize(noList, 0, 10);
        return forumService.getContents(id, noList);
    }

    @PostMapping("/content/{id}")
    @Operation(summary = "Create a content to reply the article")
    public ContentDTO createContent(@PathVariable("id") String id,
                                    @RequestBody @Validated(ForumDTO.ReplyForumGroup.class) ForumDTO forumDTO){
        forumDTO.setId(id);
        forumDTO.setAuthorId(AuthUtil.getUserId());
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
    public void likeContent(@PathVariable("id") String id,
                               @PathVariable("no") int no,
                               @RequestBody @Validated(ForumDTO.LikeContentGroup.class) ForumDTO forumDTO){
        forumService.likeOrDislikeContent(id, no, AuthUtil.getUserId(), forumDTO.getLike());
    }

    @PostMapping("/image")
    @Operation(summary = "Upload a image in base64 format")
    public ForumDTO uploadImage(@RequestBody @Validated(ForumDTO.UploadImageGroup.class) ForumDTO forumDTO){
        forumDTO.setAuthorId(AuthUtil.getUserId());
        return forumService.uploadImage(forumDTO);
    }

    private <T> void validListSize(List<T> list, int min, int max){
        if(list.size() < min || list.size() > max){
            throw new AnonygramIllegalStateException(String.format("Path variable list size must be in %s ~ %s", min, max));
        }
    }

}