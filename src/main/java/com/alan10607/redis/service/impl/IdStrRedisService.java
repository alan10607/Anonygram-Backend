package com.alan10607.redis.service.impl;

import com.alan10607.leaf.util.TimeUtil;
import com.alan10607.redis.service.StringRedisService;
import com.alan10607.redis.service.ZSetRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class IdStrRedisService {
    private final StringRedisService stringRedisService;
    private static final String KEY = "data:idStr";

    public String get() {
        return stringRedisService.get(KEY);
    }

    public void set(String idStr) {
        stringRedisService.set(KEY, idStr);
    }

}
