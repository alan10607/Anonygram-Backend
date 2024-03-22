package com.ag.domain.service;

import com.ag.domain.model.Article;
import com.ag.domain.service.base.QueryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QueryArticleService extends QueryService<Article> {

    @Override
    protected Class<Article> getDoucumentClass() {
        return Article.class;
    }

//    @Override
//    protected Class<Article> getEntityClass() {
//        return Article.class;
//    }
}
