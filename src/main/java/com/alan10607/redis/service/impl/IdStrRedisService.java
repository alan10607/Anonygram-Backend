package com.alan10607.redis.service.impl;

import com.alan10607.redis.service.StringRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class IdStrRedisService {
    private final StringRedisService stringRedisService;
    private static final String KEY = "data:idStr";

    public String get() {
        return stringRedisService.getString(KEY);
    }

    public void set(String idStr) {
        stringRedisService.setString(KEY, idStr);
    }

    public void delete() {
        stringRedisService.delete(KEY);
    }
}
