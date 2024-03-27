package com.ag.domain.repository.esQuery;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.util.NamedValue;
import com.ag.domain.model.Article;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public abstract class QueryHandler<T> {
    @Autowired
    protected ElasticsearchOperations elasticsearchOperations;
    private static final int DEFAULT_PAGE_SIZE = 100;

    protected abstract Class<T> getDoucumentClass();

    public List<T> search(BoolQuery boolQuery) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery._toQuery())
                .withPageable(PageRequest.ofSize(DEFAULT_PAGE_SIZE))
                .build();

        return elasticsearchOperations.search(nativeQuery, getDoucumentClass())
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public List<T> search(BoolQuery boolQuery, SortOptions sortOptions) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery._toQuery())
                .withSort(sortOptions)
                .withPageable(PageRequest.ofSize(DEFAULT_PAGE_SIZE))
                .build();

        return elasticsearchOperations.search(nativeQuery, getDoucumentClass())
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public long count(BoolQuery boolQuery) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery._toQuery())
                .build();

        return elasticsearchOperations.count(nativeQuery, getDoucumentClass());
    }

    public List<String> searchAggregation(String aggregationName, Aggregation aggregation) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(QueryBuilders.matchAll().build()._toQuery())
                .withAggregation(aggregationName, aggregation)
                .build();

        SearchHits<T> searchHits = elasticsearchOperations.search(nativeQuery, getDoucumentClass());
        return Optional.ofNullable((ElasticsearchAggregations) searchHits.getAggregations())
                .map(elasticsearchAggregations -> elasticsearchAggregations.aggregationsAsMap().get(aggregationName))
                .map(a -> a.aggregation().getAggregate().sterms().buckets().array())
                .orElse(Collections.emptyList())
                .stream()
                .map(bucket -> bucket.key().stringValue())
                .collect(Collectors.toList());
    }


}
