package com.ag.domain.service.base;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.ag.domain.constant.UserRole;
import com.ag.domain.exception.UserNotFoundException;
import com.ag.domain.model.Article;
import com.ag.domain.model.ForumUser;
import com.ag.domain.repository.UserRepository;
import com.ag.domain.service.base.CrudServiceImpl;
import com.ag.domain.util.AuthUtil;
import com.ag.domain.util.TimeUtil;
import com.ag.domain.util.ValidationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class QueryService {

    private final ElasticsearchOperations elasticsearchOperations;

    public <T> List<T> search(Class<T> clazz, String field, String keyword) {
        Query query = QueryBuilders.term(queryBuilder -> queryBuilder.field(field).value(keyword));
        NativeQuery nativeQuery = NativeQuery.builder().withQuery(query).build();
        SearchHits<T> result = elasticsearchOperations.search(nativeQuery, clazz);
        return result
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

}
