package com.alan10607.redis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BaseRedisService {
    @Autowired
    public RedisTemplate redisTemplate;
    private final static int CONT_EXPIRE = 3600;

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public void expire(String key, long sec) {
        long randomTime = ((int) (Math.random() * 60)) + sec;
        redisTemplate.expire(key, randomTime, TimeUnit.SECONDS);
    }

}