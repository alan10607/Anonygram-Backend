package com.alan10607.redis.service;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SetRedisService extends BaseRedisService {

    public Set<String> getSet(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public void setSet(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }
}
