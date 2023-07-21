package com.alan10607.redis.service;

import com.alan10607.redis.dto.ArticleDTO;
import com.alan10607.redis.service.base.HashBaseRedisService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.alan10607.redis.constant.RedisKey.articleKey;

@Service
@AllArgsConstructor
public class ArticleRedisService {
    private final HashBaseRedisService hashBaseRedisService;
    private static final int ARTICLE_EXPIRE_SEC = 3600;

    public ArticleDTO get(String id) {
        Map<String, Object> dataMap = hashBaseRedisService.get(articleKey(id));
        return ArticleDTO.toDTO(dataMap);
    }

    public void set(ArticleDTO articleDTO) {
        Map<String, Object> dataMap = articleDTO.toMap();
        hashBaseRedisService.set(articleKey(articleDTO.getId()), dataMap);
    }

    public void delete(String id){
        hashBaseRedisService.delete(articleKey(id));
    }

    public void expire(String id) {
        hashBaseRedisService.expire(articleKey(id), ARTICLE_EXPIRE_SEC);
    }

}
