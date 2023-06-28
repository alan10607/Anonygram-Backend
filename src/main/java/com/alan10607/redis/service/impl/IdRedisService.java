package com.alan10607.redis.service.impl;

import com.alan10607.leaf.util.TimeUtil;
import com.alan10607.redis.service.ZSetRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class IdRedisService {
    private final ZSetRedisService zSetRedisService;
    private final DefaultRedisScript createIdSetScript;
    private static final String KEY = "data:idSet";
    private static final int MAX_ID_SIZE = 100;
    private static final long SCORE_BASE = 4102416000000L;//= LocalDateTime.of(2100, 1, 1, 0, 0).atZone(UTC_PLUS_8).toInstant().toEpochMilli();
    private static final long BATCH_START = 2461449600000L;//2100EpochMilli(SCORE_BASE) - 2022EpochMilli

    public List<String> get() {
        return zSetRedisService.getZSet(KEY, 0, MAX_ID_SIZE - 1);
    }

    public void set(List<String> sortedIdList) {
        Set<ZSetOperations.TypedTuple<String>> tuples = new HashSet<>();
        for(int i = 0; i < sortedIdList.size(); i++){
            tuples.add(new DefaultTypedTuple<>(
                    sortedIdList.get(i), (double) (BATCH_START + i)));
        }
        zSetRedisService.setZSet(KEY, tuples);
    }

    public void set(String id) {
        Long res = zSetRedisService.execute(createIdSetScript,
                Arrays.asList(KEY),
                id,
                Long.toString(getNowTimeScore()),
                Integer.toString(MAX_ID_SIZE));
        log.info("Create idSet from redis succeed, id={}, popSet={}", id, res == 1);
    }

    public void updateScoreToTop(String id){
        zSetRedisService.setZSet(KEY, id, getNowTimeScore());
    }


    /**
     * Reverse localDateTime for redis score, reduce time complexity from O(log n) to O(1) when zadd
     * time:   1970---------2022----------------------------2100
     * score:  SCORE_BASE---BATCH_START---------------------0
     *
     * @return
     */
    private long getNowTimeScore(){
        long epochMilli = TimeUtil.now().atZone(TimeUtil.UTC_PLUS_8).toInstant().toEpochMilli();
        return SCORE_BASE - epochMilli;
    }
}
