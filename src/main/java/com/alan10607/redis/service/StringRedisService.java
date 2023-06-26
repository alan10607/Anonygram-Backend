package com.alan10607.redis.service;

import org.springframework.stereotype.Service;

@Service
public class StringRedisService extends BaseRedisService {

    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
}
