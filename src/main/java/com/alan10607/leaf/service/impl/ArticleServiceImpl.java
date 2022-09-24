package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.constant.ArtStatusType;
import com.alan10607.leaf.dao.ArticleDAO;
import com.alan10607.leaf.dto.ArticleDTO;
import com.alan10607.leaf.model.Article;
import com.alan10607.leaf.service.ArticleService;
import com.alan10607.leaf.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ArticleServiceImpl implements ArticleService {
    private final ArticleDAO articleDAO;
    private final TimeUtil timeUtil;
    public ArticleDTO findArticle(ArticleDTO articleDTO) {
        Article article = articleDAO.findById(articleDTO.getId())
                .orElseThrow(() -> new IllegalStateException("Article id not found"));

        return new ArticleDTO(article.getId(),
                article.getTitle(),
                article.getAuthor(),
                article.getLikes(),
                article.getWord(),
                article.getCreateDate());
    }

    public void createArticle(ArticleDTO articleDTO) {
        articleDAO.findById(articleDTO.getId())
                .ifPresent((a) -> { throw new IllegalStateException("Article id already exist"); });

        articleDAO.save(new Article(articleDTO.getId(),
                articleDTO.getTitle(),
                articleDTO.getAuthor(),
                articleDTO.getLike(),
                articleDTO.getWord(),
                ArtStatusType.NORMAL.name(),
                timeUtil.now()));
    }

    public void updateArticleLike(ArticleDTO articleDTO) {
        Article article = articleDAO.findById(articleDTO.getId())
                .orElseThrow(() -> new IllegalStateException("Article id not found"));

        article.setLikes(articleDTO.getLike());
        articleDAO.save(article);
    }

    public void deleteArticle(ArticleDTO articleDTO) {
        Article article = articleDAO.findById(articleDTO.getId())
                .orElseThrow(() -> new IllegalStateException("Article id not found"));

        articleDAO.delete(article);
    }
}
