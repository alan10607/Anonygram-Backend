package com.alan10607.redis.service.impl;

import com.alan10607.leaf.dto.ContentDTO;
import com.alan10607.redis.service.HashRedisService;
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
        Map<String, Object> dataMap = hashRedisService.getHash(getKey(id, no));
        return ContentDTO.toDTO(dataMap);
    }

    public void set(ContentDTO contentDTO) {
        Map<String, Object> dataMap = contentDTO.toMap();
        hashRedisService.setHash(getKey(contentDTO.getId(), contentDTO.getNo()), dataMap);
    }

    public void delete(String id, int no){
        hashRedisService.delete(getKey(id, no));
    }

    public void expire(String id, int no) {
        hashRedisService.expire(getKey(id, no), CONTENT_EXPIRE_SEC);
    }

    public void increaseLikes(String id, int no, long addNum) {
        hashRedisService.increment(getKey(id, no), "likes", addNum);
    }

}
