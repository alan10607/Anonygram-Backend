package com.alan10607.redis.service.impl;

import com.alan10607.redis.service.RedisService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class HashRedisService extends BaseRedisService implements RedisService<Map<String, Object>> {

    @Override
    public Map<String, Object> get(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public void set(String key, Map<String, Object> data) {
        redisTemplate.opsForHash().putAll(key, data);
    }
}
