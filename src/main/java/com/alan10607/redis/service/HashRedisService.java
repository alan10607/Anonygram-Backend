package com.alan10607.redis.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class HashRedisService extends BaseRedisService {

    public Map<String, Object> getHash(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public void setHash(String key, Map<String, Object> value) {
        redisTemplate.opsForHash().putAll(key, value);
    }

    public void increment(String key, String hashKey, long addNum){
        redisTemplate.opsForHash().increment(key, hashKey, addNum);
    }
}
