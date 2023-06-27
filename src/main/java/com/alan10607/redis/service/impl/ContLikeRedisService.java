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

    public Long checkLike(String id, int no, String userId){
        String isLike = getLikeValue(id, no, userId, Status.LIKE);
        String unLike = getLikeValue(id, no, userId, Status.UNLIKE);
        return setRedisService.execute(checkLikeScript,
                Arrays.asList(Key.NEW.value, Key.BATCH.value, Key.STATIC.value),
                isLike, unLike);
    }

    public void set(Key contLikeKey, String contId, int no, String userId, Status likeStatus) {
        setRedisService.set(contLikeKey.value, getLikeValue(contId, no, userId, likeStatus));
    }

    public boolean UpdateUnLikeFromRedis(String id, int no, String userId) {
        Long isSuccess = setRedisService.execute(toggleLikeScript,
                Arrays.asList(Key.NEW.value, Key.BATCH.value, Key.STATIC.value),
                getLikeValue(id, no, userId, Status.UNLIKE),
                getLikeValue(id, no, userId, Status.LIKE));

        if(isSuccess == 0){
            log.error("Already unlike, skip this time, id={}, no={}, userId={}", id, no, userId);
        }else if(isSuccess == -1) {
            throw new RuntimeException(
                    String.format("Update like by lua failed because set not found, isSuccess=-1, id=%s, no=%s, userId=%s", id, no, userId));
        }

        return isSuccess == 1;
    }


}
