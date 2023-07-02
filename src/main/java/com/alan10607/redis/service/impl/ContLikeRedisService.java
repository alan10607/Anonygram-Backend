package com.alan10607.redis.service.impl;

import com.alan10607.redis.service.StringRedisService;
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
    private final StringRedisService stringRedisService;
    private final DefaultRedisScript getContentLikeScript;
    private final DefaultRedisScript setContentLikeScript;

    @AllArgsConstructor
    public enum KeyType {
        STATIC("static"),
        NEW("new");
        private final String value;
    }

    @AllArgsConstructor
    public enum LikeStatus {
        LIKE("1"), DISLIKE("0");
        private final String value;
    }

    private String getKey(String id, int no, KeyType keyType, String userId) {
        return String.format("data:cont:%s:%s:like:%s:%s", id, no, keyType.value, userId);
    }

    public boolean get(String id, int no, String userId){
        Long isLike = stringRedisService.execute(getContentLikeScript,
                getKeyList(id, no, userId));
        return isLike == 1;
    }

    public boolean set(String id, int no, String userId, LikeStatus likeStatus) {
        Long isSuccess = stringRedisService.execute(setContentLikeScript,
                getKeyList(id, no, userId),
                likeStatus.value);

        if(isSuccess == 0){
            log.info("Already {}, skip this time, id={}, no={}, userId={}", likeStatus.name(), id, no, userId);
        }

        return isSuccess == 1;
    }

    public void set(String contId, int no, KeyType keyType, String userId, LikeStatus likeStatus) {
        stringRedisService.setString(getKey(contId, no, keyType, userId), likeStatus.value);
    }

    private List getKeyList(String id, int no, String userId){
        return Arrays.asList(getKey(id, no, KeyType.NEW, userId),
                            getKey(id, no, KeyType.STATIC, userId));
    }


}
