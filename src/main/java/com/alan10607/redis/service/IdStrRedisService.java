package com.alan10607.redis.service;

import com.alan10607.redis.service.base.StringBaseRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class IdStrRedisService {
    private final StringBaseRedisService stringBaseRedisService;
    private static final String KEY = "data:idStr";

    public String get() {
        return stringBaseRedisService.get(KEY);
    }

    public void set(String idStr) {
        stringBaseRedisService.set(KEY, idStr);
    }

    public void delete() {
        stringBaseRedisService.delete(KEY);
    }
}
