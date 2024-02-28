package com.ag.domain.controller;

import com.ag.domain.dto.ArticleDTO;
import com.ag.domain.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "article")
@AllArgsConstructor
@Tag(name = "Anonygram Forum")
public class ArticleController {


    private final ArticleService articleService;

    @GetMapping("/{idList}/{noList}")
    @Operation(summary = "Get articles and contents in matrix")
    public List<ArticleDTO> get(@PathVariable("idList") List<String> idList,
                                @PathVariable("noList") List<Integer> noList){
        return articleService.get(idList, noList);
    }
//
//    @PostMapping("/article")
//    @Operation(summary = "Create a article with first content")
//    public ArticleDTO createArticleWithContent(@RequestBody @Validated(ForumDTO.CreateArticleGroup.class) ForumDTO forumDTO){
//        ContentDTO contentDTO = new ContentDTO(forumDTO.getWord());
//        ArticleDTO articleDTO = new ArticleDTO(forumDTO.getTitle(), Collections.singletonList(contentDTO));
//        String id = forumWriteService.createArticleWithContent(articleDTO);
//        return forumReadService.getArticleWithContent(id, 0);
//    }
//
//    @PostMapping("/article/{id}")
//    @Operation(summary = "Create a content under article")
//    public ArticleDTO createContent(@PathVariable("id") String id,
//                                    @RequestBody @Validated(ForumDTO.ReplyForumGroup.class) ForumDTO forumDTO){
//        ContentDTO contentDTO = new ContentDTO(id, forumDTO.getWord());
//        int no = forumWriteService.createContent(contentDTO);
//        return forumReadService.getArticleWithContent(id, no);
//    }
//
//    @DeleteMapping("/article/{id}/{no}")
//    @Operation(summary = "Delete a content. If delete first content, will also delete its article")
//    public void deleteContent(@PathVariable("id") String id,
//                              @PathVariable("no") int no){
//        forumWriteService.deleteContent(id, no);
//    }
//
//    @PatchMapping("/article/{id}/{no}/like")
//    @Operation(summary = "To like a content")
//    public void updateContentLike(@PathVariable("id") String id,
//                                  @PathVariable("no") int no,
//                                  @RequestBody @Validated(ForumDTO.LikeContentGroup.class) ForumDTO forumDTO){
//        forumWriteService.updateContentLike(id, no, forumDTO.getLike());
//    }

}