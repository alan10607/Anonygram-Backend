package com.alan10607.redis.service;

import org.springframework.stereotype.Service;

@Service
public class StringRedisService extends BaseRedisService {

    public String getString(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void setString(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
}
