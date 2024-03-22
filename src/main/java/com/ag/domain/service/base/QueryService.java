package com.ag.domain.service.base;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public abstract class QueryService<T> {
    @Autowired
    protected ElasticsearchOperations elasticsearchOperations;

    protected abstract Class<T> getDoucumentClass();

    public List<T> search(String field, String keyword) {
        Query query = QueryBuilders.term(queryBuilder -> queryBuilder.field(field).value(keyword));
        NativeQuery nativeQuery = NativeQuery.builder().withQuery(query).build();
        SearchHits<T> result = elasticsearchOperations.search(nativeQuery, getDoucumentClass());
        return result
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

}
