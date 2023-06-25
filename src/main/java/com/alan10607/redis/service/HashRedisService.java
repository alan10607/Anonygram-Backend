package com.alan10607.redis.service;

import com.alan10607.redis.service.BaseRedisService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class HashRedisService extends BaseRedisService {

    public Map<String, Object> get(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public void set(String key, Map<String, Object> value) {
        redisTemplate.opsForHash().putAll(key, value);
    }
}
