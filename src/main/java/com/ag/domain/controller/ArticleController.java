package com.ag.domain.controller;

import com.ag.domain.dto.ArticleDTO;
import com.ag.domain.model.Article;
import com.ag.domain.service.ArticleService;
import com.ag.domain.util.PojoFiledUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Tag(name = "Article")
@RequestMapping(path = "article")
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/{articleId}/{no}")
    @Operation(summary = "Get an article")
    public ArticleDTO get(@PathVariable("articleId") String articleId,
                          @PathVariable("no") Integer no) {
        return PojoFiledUtil.convertObject(articleService.get(articleId, no), ArticleDTO.class);
    }

    @PostMapping()
    @Operation(summary = "Create a new article")
    public ArticleDTO create(@RequestBody ArticleDTO articleDTO) {
        Article article = PojoFiledUtil.convertObject(articleDTO, Article.class);
        article.setArticleId(null);
        article.setNo(0);
        return PojoFiledUtil.convertObject(articleService.create(article), ArticleDTO.class);
    }

    @PostMapping("/{articleId}")
    @Operation(summary = "Create a reply article")
    public ArticleDTO createReply(@PathVariable("articleId") String articleId,
                                  @RequestBody ArticleDTO articleDTO) {
        Article article = PojoFiledUtil.convertObject(articleDTO, Article.class);
        article.setArticleId(articleId);
        article.setNo(null);
        return PojoFiledUtil.convertObject(articleService.create(article), ArticleDTO.class);
    }

    @PatchMapping("/{articleId}/{no}")
    @Operation(summary = "To patch an article")
    public void patch(@PathVariable("articleId") String articleId,
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

    @DeleteMapping("/{articleId}/{no}")
    @Operation(summary = "Delete an article. If delete the first content, will also delete all replied articles")
    public void delete(@PathVariable("articleId") String articleId,
                       @PathVariable("no") int no) {
        articleService.delete(articleId, no);
    }

}