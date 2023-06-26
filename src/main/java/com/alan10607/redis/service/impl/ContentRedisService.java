package com.alan10607.redis.service.impl;

import com.alan10607.leaf.dto.ContentDTO;
import com.alan10607.redis.service.HashRedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class ContentRedisService {
    private final HashRedisService hashRedisService;
    private static final int CONTENT_EXPIRE_SEC = 3600;

    private String getKey(String id, int no) {
        return String.format("data:cont:%s:%s", id, no);
    }

    public ContentDTO get(String id, int no) {
        Map<String, Object> dataMap = hashRedisService.get(getKey(id, no));
        return new ObjectMapper().convertValue(dataMap, ContentDTO.class);
    }

    public void set(ContentDTO contentDTO) {
        Map<String, Object> dataMap = new ObjectMapper().convertValue(contentDTO, Map.class);
        hashRedisService.set(getKey(contentDTO.getId(), contentDTO.getNo()), dataMap);
    }

    public void expire(String id, int no) {
        hashRedisService.expire(getKey(id, no), CONTENT_EXPIRE_SEC);
    }

}
