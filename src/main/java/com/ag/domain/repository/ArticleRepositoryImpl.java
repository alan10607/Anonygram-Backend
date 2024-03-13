//package com.ag.domain.repository;
//
//import com.ag.domain.model.Article;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.elasticsearch.client.erhlc.ElasticsearchRestTemplate;
//import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
//import org.springframework.data.elasticsearch.core.IndexOperations;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;
//import org.springframework.data.elasticsearch.repository.support.SimpleElasticsearchRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//public class ArticleRepositoryImpl<T, ID> extends SimpleElasticsearchRepository<T, ID> implements CustomRepository<T, ID> {
//
//    public ArticleRepositoryImpl(ElasticsearchEntityInformation<T, ID> metadata, ElasticsearchOperations operations) {
//        super(metadata, operations);
//    }
//
//    @Override
//    public Optional<Article> findByArticleIdAndNo(String articleId, int no) {
//        return Optional.empty();
//    }
//}
