package com.alan10607.leaf.service;

import com.alan10607.leaf.dto.ArticleDTO;

import java.util.List;

public interface ArticleService {
    ArticleDTO findArticle(ArticleDTO articleDTO);
    void createArticle(ArticleDTO articleDTO);
    void updateArticleLike(ArticleDTO articleDTO);
    void deleteArticle(ArticleDTO articleDTO);
}
