package com.alan10607.redis.service;

import com.alan10607.redis.constant.RedisKey;
import com.alan10607.redis.service.base.StringBaseRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserNameRedisService {
    private final StringBaseRedisService stringBaseRedisService;
    private static final int USER_NAME_EXPIRE_SEC = 3600;

    private String getKey(String userId) {
        return String.format(RedisKey.USER, userId);
    }

    public String get(String userId){
        return stringBaseRedisService.get(getKey(userId));
    }

    public void set(String userId, String name) {
        stringBaseRedisService.set(getKey(userId), name);
    }

    public void expire(String userId) {
        stringBaseRedisService.expire(getKey(userId), USER_NAME_EXPIRE_SEC);
    }


}
