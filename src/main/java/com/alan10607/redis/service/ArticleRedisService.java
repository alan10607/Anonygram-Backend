package com.alan10607.redis.service;

import com.alan10607.leaf.dto.ArticleDTO;
import com.alan10607.redis.service.base.HashBaseRedisService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class ArticleRedisService {
    private final HashBaseRedisService hashBaseRedisService;
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
        Map<String, Object> dataMap = hashBaseRedisService.get(getKey(id));
        return ArticleDTO.toDTO(dataMap);
    }

    public void set(ArticleDTO articleDTO) {
        Map<String, Object> dataMap = articleDTO.toMap();
        hashBaseRedisService.set(getKey(articleDTO.getId()), dataMap);
    }

    public void delete(String id){
        hashBaseRedisService.delete(getKey(id));
    }

    public void expire(String id) {
        hashBaseRedisService.expire(getKey(id), ARTICLE_EXPIRE_SEC);
    }

}
