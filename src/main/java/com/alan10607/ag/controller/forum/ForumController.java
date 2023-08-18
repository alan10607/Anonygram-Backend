package com.alan10607.ag.controller.forum;

import com.alan10607.ag.constant.StatusType;
import com.alan10607.ag.dto.ArticleDTO;
import com.alan10607.ag.dto.ContentDTO;
import com.alan10607.ag.dto.ForumDTO;
import com.alan10607.ag.exception.AnonygramIllegalStateException;
import com.alan10607.ag.service.forum.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/forum")
@AllArgsConstructor
@Tag(name = "Anonygram Forum")
public class ForumController {
    private final IdService idService;
    private final ArticleService articleService;
    private final ContentService contentService;
    private final LikeService likeService;
    private final ImgurService imgurService;

    @GetMapping("/id")
    @Operation(summary = "Get all ids of article")
    public List<String> getId(){
        return idService.get();
    }

    @GetMapping("/article/{id}")
    @Operation(summary = "Get a article with the first content")
    public ArticleDTO getArticle(@PathVariable("id") String id){
        return articleService.get(id);
    }

    @GetMapping("/articles/{idList}")
    @Operation(summary = "Get article list with the first content")
    public List<ArticleDTO> getArticles(@PathVariable("idList") List<String> idList){
        validListSize(idList, 0, 10);
        return articleService.get(idList);

    }

    @PostMapping("/article")
    @Operation(summary = "Create a article with the original poster content")
    public ArticleDTO createArticle(@RequestBody @Validated(ForumDTO.CreateArticleGroup.class) ForumDTO forumDTO){
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setTitle(forumDTO.getTitle());
        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setWord(forumDTO.getWord());
        articleDTO.setContentList(Collections.singletonList(contentDTO));
        String id = articleService.create(articleDTO);
        return articleService.get(id);
    }

    @GetMapping("/content/{id}/{no}")
    @Operation(summary = "Get a content")
    public ContentDTO getContent(@PathVariable("id") String id,
                                 @PathVariable("no") int no){
        ArticleDTO articleDTO = articleService.get(id);
        if(articleDTO.getStatus() != StatusType.NORMAL){
            return new ContentDTO(id, no, articleDTO.getStatus());
        }
        return contentService.get(id, no);
    }

    @GetMapping("/contents/{id}/{noList}")
    @Operation(summary = "Get contents list")
    public List<ContentDTO> getContents(@PathVariable("id") String id,
                                        @PathVariable("noList") List<Integer> noList){
        validListSize(noList, 0, 10);
        ArticleDTO articleDTO = articleService.get(id);
        if(articleDTO.getStatus() != StatusType.NORMAL){
            return noList.stream()
                    .map(no -> new ContentDTO(id, no, articleDTO.getStatus()))
                    .collect(Collectors.toList());
        }
        return contentService.get(id, noList);
    }

    @PostMapping("/content/{id}")
    @Operation(summary = "Create a content to reply the article")
    public ContentDTO createContent(@PathVariable("id") String id,
                                    @RequestBody @Validated(ForumDTO.ReplyForumGroup.class) ForumDTO forumDTO){
        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setId(id);
        contentDTO.setWord(forumDTO.getWord());
        int no = contentService.create(contentDTO);
        return contentService.get(id, no);
    }

    @DeleteMapping("/content/{id}/{no}")
    @Operation(summary = "Delete a content. If delete first content, will also delete article")
    public void deleteContent(@PathVariable("id") String id,
                              @PathVariable("no") int no){
        if(no == 0){
            articleService.updateStatus(id, StatusType.DELETED);
        }else{
            contentService.updateStatus(id, no, StatusType.DELETED);
        }
    }

    @PatchMapping("/like/{id}/{no}")
    @Operation(summary = "To like a content")
    public void updateLike(@PathVariable("id") String id,
                               @PathVariable("no") int no,
                               @RequestBody @Validated(ForumDTO.LikeContentGroup.class) ForumDTO forumDTO){
        contentService.updateLike(id, no, forumDTO.getLike());
    }

    @PostMapping("/image")
    @Operation(summary = "Upload a image in base64 format")
    public ForumDTO uploadImage(@RequestBody @Validated(ForumDTO.UploadImageGroup.class) ForumDTO forumDTO){
        String imageUrl = imgurService.upload(forumDTO.getImageBase64());
        forumDTO.setImageUrl(imageUrl);
        forumDTO.setImageBase64(null);//to reduce payload size
        return forumDTO;
    }

    private <T> void validListSize(List<T> list, int min, int max){
        if(list.size() < min || list.size() > max){
            throw new AnonygramIllegalStateException(String.format("Path variable list size must be in %s ~ %s", min, max));
        }
    }

}