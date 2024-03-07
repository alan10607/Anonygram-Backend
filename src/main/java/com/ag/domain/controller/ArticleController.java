package com.ag.domain.controller;

import com.ag.domain.constant.StatusType;
import com.ag.domain.dto.ArticleDTO;
import com.ag.domain.model.Article;
import com.ag.domain.model.Like;
import com.ag.domain.service.ArticleService;
import com.ag.domain.service.LikeService;
import com.ag.domain.util.PojoFiledUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "article")
@AllArgsConstructor
@Tag(name = "Anonygram Forum")
@Slf4j
public class ArticleController {
    private final ArticleService articleService;
    private final LikeService likeService;


    @GetMapping("/{id}/{no}")
    @Operation(summary = "Get a article")
    public ArticleDTO get(@PathVariable("id") String id,
                          @PathVariable("no") Integer no) {
        Article article = articleService.get(id, no);
        return outputFilter(article);
    }

    @PostMapping()
    @Operation(summary = "Create a article with first content")
    public ArticleDTO create(@RequestBody ArticleDTO articleDTO) {
        Article article = PojoFiledUtil.convertObject(articleDTO, Article.class);
        article = articleService.create(article);
        return outputFilter(article);
    }

    @PostMapping("/{id}")
    @Operation(summary = "Create a content under article")
    public void createContent(@PathVariable("id") String id,
                              @RequestBody ArticleDTO articleDTO) {
        Article article = PojoFiledUtil.convertObject(articleDTO, Article.class);
        article.setId(id);
        articleService.create(article);
    }

    @PatchMapping("/{id}/{no}/word")
    @Operation(summary = "To modify a content word")
    public void patchWord(@PathVariable("id") String id,
                          @PathVariable("no") int no,
                          @RequestBody ArticleDTO articleDTO) {
        Article article = PojoFiledUtil.convertObject(articleDTO, Article.class);
        article.setId(id);
        article.setNo(no);
        articleService.patchWord(article);
    }

    @PatchMapping("/{id}/{no}/like")
    @Operation(summary = "To like a content")
    public void patchLike(@PathVariable("id") String id,
                          @PathVariable("no") int no,
                          @RequestBody ArticleDTO articleDTO) {
        Like like = new Like(id, no, articleDTO.getLike());
        likeService.update(like);
    }

    @DeleteMapping("/{id}/{no}")
    @Operation(summary = "Delete a content. If delete first content, will also delete its article")
    public void patchStatusToDelete(@PathVariable("id") String id,
                                    @PathVariable("no") int no) {
        Article article = new Article(id, no);
        article.setStatus(StatusType.DELETED);
        articleService.patchStatus(article);
    }

    private ArticleDTO outputFilter(Article article) {
        ArticleDTO articleDTO = PojoFiledUtil.convertObject(article, ArticleDTO.class);
        return articleDTO;
//        switch (articleDTO.getStatus()) {
//            case NORMAL:
//                //TODO: prepare after get
//                Like like = likeService.get(article.getId(), article.getNo(), "userId");
//                articleDTO.setLike(like.getState());
//
////                UserDTO userDTO = userService.get(contentDTO.getAuthorId());
////                contentDTO.setAuthorName(userDTO.getUsername());
////                contentDTO.setAuthorHeadUrl(userDTO.getHeadUrl());
////                contentDTO.setLike(likeService.get(contentDTO.getId(), contentDTO.getNo(), AuthUtil.getUserId()));
//                return articleDTO;
//            case DELETED:
//                return new ArticleDTO(article.getId(), article.getNo(), StatusType.DELETED);
//            case UNKNOWN:
//            default:
//                log.info("article {}/{} not found", article.getId(), article.getNo());
//                return new ArticleDTO(article.getId(), article.getNo(), StatusType.DELETED);
//        }
    }


}