package com.ag.domain.repository;

import com.ag.domain.model.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface ArticleRepository extends ElasticsearchRepository<Article, String> {
    Optional<Article> findByArticleIdAndNo(String articleId, int no);
}
