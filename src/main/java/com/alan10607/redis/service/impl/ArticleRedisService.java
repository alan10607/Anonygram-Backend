package com.alan10607.redis.service.impl;

import com.alan10607.leaf.dto.ArticleDTO;
import com.alan10607.redis.service.HashRedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class ArticleRedisService {
    private final HashRedisService hashRedisService;
    private static final int ARTICLE_EXPIRE_SEC = 3600;

    private String getKey(String id){
        return String.format("data:art:%s", id);
    }

    public ArticleDTO get(String id) {
        Map<String, Object> dataMap = hashRedisService.get(getKey(id));
        return new ObjectMapper().convertValue(dataMap, ArticleDTO.class);
    }

    public void set(ArticleDTO articleDTO) {
        Map<String, Object> dataMap = new ObjectMapper().convertValue(articleDTO, Map.class);
        hashRedisService.set(getKey(articleDTO.getId()), dataMap);
    }

    public void expire(String id) {
        hashRedisService.expire(getKey(id), ARTICLE_EXPIRE_SEC);
    }

}
