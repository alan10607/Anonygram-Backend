package com.alan10607.redis.service.impl;

import com.alan10607.leaf.util.TimeUtil;
import com.alan10607.redis.service.ZSetRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class IdRedisService {
    private final ZSetRedisService zSetRedisService;
    private final DefaultRedisScript createIdSetScript;
    private final static String KEY = "data:idSet";
    private final static int MAX_ID_SIZE = 100;
    private final static long SCORE_BASE = 4102416000000L;//= LocalDateTime.of(2100, 1, 1, 0, 0).atZone(UTC_PLUS_8).toInstant().toEpochMilli();
    private final static long BATCH_START = 2461449600000L;//2100EpochMilli(SCORE_BASE) - 2022EpochMilli

    public List<String> get() {
        return zSetRedisService.get(KEY, 0, MAX_ID_SIZE - 1);
    }

    public void set(List<String> sortedIdList) {
        Set<ZSetOperations.TypedTuple<String>> tuples = new HashSet<>();
        for(int i = 0; i < sortedIdList.size(); i++){
            tuples.add(new DefaultTypedTuple<>(
                    sortedIdList.get(i), (double) (TimeUtil.BATCH_START + i)));
        }
        zSetRedisService.set(KEY, tuples);
    }

    public void set(String id, LocalDateTime updateTime) {
        boolean res = zSetRedisService.execute(createIdSetScript,
                Arrays.asList(KEY),
                id,
                Long.toString(getRedisScore(updateTime)),
                Integer.toString(MAX_ID_SIZE));
        log.info("Create idSet from redis succeed, id={}, popSet={}", id, res);
    }

    public void updateScore(String id, LocalDateTime updateTime){
        zSetRedisService.set(KEY, id, getRedisScore(updateTime));
    }


    /**
     * Reverse localDateTime for redis score, reduce time complexity from O(log n) to O(1) when zadd
     * time:   1970---------2022----------------------------2100
     * score:  SCORE_BASE---BATCH_START---------------------0
     *
     * @return
     */
    private long getRedisScore(LocalDateTime localDateTime){
        long epochMilli = localDateTime.atZone(TimeUtil.UTC_PLUS_8).toInstant().toEpochMilli();
        return SCORE_BASE - epochMilli;
    }
}
