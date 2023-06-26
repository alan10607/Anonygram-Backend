package com.alan10607.redis.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class SetRedisService extends BaseRedisService {

    public Set<String> get(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public void set(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }
}
