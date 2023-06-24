package com.alan10607.redis.service.impl;

import com.alan10607.leaf.dto.ArticleDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ArticleRedisService extends HashRedisService{
    private String getKey(String id){
        return String.format("data:art:%s", id);
    }

    public ArticleDTO get(String id, int m) {
        Map<String, Object> dataMap = super.get(getKey(id));
        return new ObjectMapper().convertValue(dataMap, ArticleDTO.class);
    }

    public void set(ArticleDTO articleDTO) {
        Map<String, Object> dataMap = new ObjectMapper().convertValue(articleDTO, Map.class);
        redisTemplate.opsForHash().putAll(getKey(articleDTO.getId()), dataMap);
    }

}
