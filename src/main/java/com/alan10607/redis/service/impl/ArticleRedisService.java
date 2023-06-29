package com.alan10607.redis.service.impl;

import com.alan10607.leaf.dto.ArticleDTO;
import com.alan10607.redis.service.HashRedisService;
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

    /*
    TODO:
     4.extend BaseRedisService
     5.study controller unit test
    */

    public ArticleDTO get(String id) {
        Map<String, Object> dataMap = hashRedisService.getHash(getKey(id));
        return ArticleDTO.toDTO(dataMap);
    }

    public void set(ArticleDTO articleDTO) {
        Map<String, Object> dataMap = articleDTO.toMap();
        hashRedisService.setHash(getKey(articleDTO.getId()), dataMap);
    }

    public void delete(String id){
        hashRedisService.delete(getKey(id));
    }

    public void expire(String id) {
        hashRedisService.expire(getKey(id), ARTICLE_EXPIRE_SEC);
    }

}
