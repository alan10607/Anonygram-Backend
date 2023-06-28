package com.alan10607.redis.service.impl;

import com.alan10607.redis.service.SetRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ContLikeRedisService {
    private final SetRedisService setRedisService;
    private final DefaultRedisScript checkLikeScript;
    private final DefaultRedisScript toggleLikeScript;

    @AllArgsConstructor
    public enum KeyType {
        STATIC("static"),
        BATCH("batch"),
        NEW("new");
        private final String value;
    }

    @AllArgsConstructor
    private enum LikeStatus {
        LIKE(1), UNLIKE(0);
        private final int value;
    }

    private String getKey(String id, int no, KeyType keyType) {
        return String.format("data:cont:%s:%s:like:%s", id, no, keyType.value);
    }

    private String getValue(String userId, LikeStatus likeStatus){
        return String.format("%s:%s", userId, likeStatus.value);
    }

    public Long checkLike(String id, int no, String userId){
        return setRedisService.execute(checkLikeScript,
                getNewBatchStaticKey(id, no),
                userId);
    }

    public void set(String contId, int no, KeyType keyType, String userId, LikeStatus likeStatus) {
        setRedisService.setSet(getKey(contId, no, keyType), getValue(userId, likeStatus));
    }

    public boolean UpdateUnLikeFromRedis(String id, int no, String userId, LikeStatus likeStatus) {
        Long isSuccess = setRedisService.execute(toggleLikeScript,
                getNewBatchStaticKey(id, no),
                userId, likeStatus);

        if(isSuccess == 0){
            log.error("Already unlike, skip this time, id={}, no={}, userId={}", id, no, userId);
        }else if(isSuccess == -1) {
            throw new RuntimeException(
                String.format("Update like by lua failed because set not found, isSuccess=-1, id=%s, no=%s, userId=%s", id, no, userId));
        }

        return isSuccess == 1;
    }

    private List getNewBatchStaticKey(String id, int no){
        return Arrays.asList(getKey(id, no, KeyType.NEW),
                            getKey(id, no, KeyType.BATCH),
                            getKey(id, no, KeyType.STATIC));
    }


}
