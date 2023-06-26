package com.alan10607.redis.service.impl;

import com.alan10607.redis.service.SetRedisService;
import com.alan10607.redis.service.StringRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@AllArgsConstructor
@Slf4j
public class ContLikeRedisService {
    private final SetRedisService setRedisService;
    private final DefaultRedisScript checkLikeScript;
    private final DefaultRedisScript toggleLikeScript;
    public static final String STATIC_KEY = "data:like:static";
    public static final String NEW_KEY = "data:like:new";
    public static final String BATCH_KEY = "data:like:batch";

    @AllArgsConstructor
    public enum Key {
        STATIC("data:like:static"),
        NEW("data:like:new"),
        BATCH("data:like:batch");
        private final String value;
    }

    @AllArgsConstructor
    private enum Status {
        LIKE(1), UNLIKE(0);
        private final int value;
    }

    public String getLikeValue(String contId, int no, String userId, Status likeStatus){
        return String.format("%s:%s:%s:%s", contId, no, userId, likeStatus.value);
    }

    public boolean checkLike(String id, int no, String userId){
        String isLike = getLikeValue(id, no, userId, Status.LIKE);
        String unLike = getLikeValue(id, no, userId, Status.UNLIKE);
        return setRedisService.execute(checkLikeScript,
                Arrays.asList(Key.NEW.value, Key.BATCH.value, Key.STATIC.value),
                isLike, unLike);
    }

    public void set(Key contLikeKey, String contId, int no, String userId, Status likeStatus) {
        setRedisService.set(contLikeKey.value, getLikeValue(contId, no, userId, likeStatus));
    }


}
