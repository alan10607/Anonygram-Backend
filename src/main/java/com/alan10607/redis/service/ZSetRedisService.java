package com.alan10607.redis.service;

import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ZSetRedisService extends BaseRedisService {

    public List<String> getZSet(String key, int start, int end) {
        Set<String> zSet = redisTemplate.opsForZSet().range(key, start, end);
        return zSet.stream().collect(Collectors.toList());
    }

    public void setZSet(String key, Set<ZSetOperations.TypedTuple<String>> tuples) {
        redisTemplate.opsForZSet().add(key, tuples);
    }

    public void setZSet(String key, String value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }


}
