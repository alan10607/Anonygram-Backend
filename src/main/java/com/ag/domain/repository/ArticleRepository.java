package com.ag.domain.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import com.ag.domain.model.Article;
import com.ag.domain.model.Like;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

public interface ArticleRepository extends ElasticsearchRepository<Article, String> {

}

//
//@Service
//@AllArgsConstructor
//@Slf4j
//public class ArticleRepository {
//    private final ElasticsearchClient client;
//    private final String indexName = "article";
//
//    @PostConstruct
//    public void init() {
//        createIndex();
//    }
//
//    private void createIndex() {
//        var request = new CreateIndexRequest.Builder()
//                .index(indexName)
//                .build();
//
//        try {
//            client.indices().create(request);
//            //TODO: need test
//            this.save(new Article());
//        } catch (Exception e) {
//            log.error(e.getMessage());
////            throw new RuntimeException(e);
//        }
//    }
//
//    public Article save(Article doc) {
//        var request = new IndexRequest.Builder<Article>()
//                .index(indexName)
//                .id(doc.getId())
//                .document(doc)
//                .build();
//
//        IndexResponse indexResponse = null;
//        try {
//            indexResponse = client.index(request);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        doc.setId(indexResponse.id());
//        return doc;
//    }
//
//    public Article get(Article article) {
//        return null;
//    }
//    public Article delete(Article article) {
//        return null;
//    }
//
//
//    public Article get(String id, Integer no) {
//        return null;
//    }
//}
