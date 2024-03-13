package com.ag.domain.controller;

import com.ag.domain.constant.StatusType;
import com.ag.domain.dto.ArticleDTO;
import com.ag.domain.model.Article;
import com.ag.domain.model.Like;
import com.ag.domain.service.ArticleService;
import com.ag.domain.service.LikeService;
import com.ag.domain.util.AuthUtil;
import com.ag.domain.util.PojoFiledUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Tag(name = "Anonygram Forum")
@RequestMapping(path = "article")
public class ArticleController {
    private final ArticleService articleService;
    private final LikeService likeService;


    @GetMapping("/{articleId}/{no}")
    @Operation(summary = "Get an article")
    public ArticleDTO get(@PathVariable("articleId") String articleId,
                          @PathVariable("no") Integer no) {
        return outputFilter(articleService.get(articleId, no));
    }

    @PostMapping()
    @Operation(summary = "Create a new article")
    public ArticleDTO create(@RequestBody ArticleDTO articleDTO) {
        Article article = PojoFiledUtil.convertObject(articleDTO, Article.class);
        article.setArticleId(null);
        article.setNo(0);
        return outputFilter(articleService.create(article));
    }

    @PostMapping("/{articleId}")
    @Operation(summary = "Create a reply article")
    public ArticleDTO createReply(@PathVariable("articleId") String articleId,
                                  @RequestBody ArticleDTO articleDTO) {
        Article article = PojoFiledUtil.convertObject(articleDTO, Article.class);
        article.setArticleId(articleId);
        article.setNo(null);
        return outputFilter(articleService.create(article));
    }

    @PatchMapping("/{articleId}/{no}")
    @Operation(summary = "To patch an article")
    public void update(@PathVariable("articleId") String articleId,
                       @PathVariable("no") int no,
                       @RequestBody ArticleDTO articleDTO) {
        Article article = PojoFiledUtil.convertObject(articleDTO, Article.class);
        article.setArticleId(articleId);
        article.setNo(no);
        articleService.patch(article);
    }

    @PatchMapping("/{articleId}/{no}/title")
    @Operation(summary = "To patch the first article title")
    public void patchTitle(@PathVariable("articleId") String articleId,
                           @PathVariable("no") int no,
                           @RequestBody ArticleDTO articleDTO) {
        Article article = PojoFiledUtil.convertObject(articleDTO, Article.class);
        article.setArticleId(articleId);
        article.setNo(no);
        article = PojoFiledUtil.retainFields(article, "articleId", "no", "title");
        articleService.patch(article);
    }

    @PatchMapping("/{articleId}/{no}/word")
    @Operation(summary = "To patch an article word")
    public void patchWord(@PathVariable("articleId") String articleId,
                          @PathVariable("no") int no,
                          @RequestBody ArticleDTO articleDTO) {
        Article article = PojoFiledUtil.convertObject(articleDTO, Article.class);
        article.setArticleId(articleId);
        article.setNo(no);
        article = PojoFiledUtil.retainFields(article, "articleId", "no", "word");
        articleService.patch(article);
    }

    @PatchMapping("/{articleId}/{no}/like")
    @Operation(summary = "To like a content")
    public void patchLike(@PathVariable("articleId") String articleId,
                          @PathVariable("no") int no,
                          @RequestBody ArticleDTO articleDTO) {
        Like like = new Like(articleId, no, AuthUtil.getUserId());
        if (articleDTO.getLike()) {
            likeService.create(like);
        } else {
            likeService.delete(like);
        }
    }

    @DeleteMapping("/{articleId}/{no}")
    @Operation(summary = "Delete an article. If delete the first content, will also delete all replied articles")
    public void delete(@PathVariable("articleId") String articleId,
                       @PathVariable("no") int no) {
        articleService.delete(articleId, no);
    }

    private ArticleDTO outputFilter(Article article) {
        if(article == null) return new ArticleDTO();
        ArticleDTO articleDTO = PojoFiledUtil.convertObject(article, ArticleDTO.class);
        articleDTO.setLike(isLike(article));
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

    private boolean isLike(Article article){
        return likeService.get(article.getArticleId(), article.getNo(), AuthUtil.getUserId()) != null;
    }


}