package com.alan10607.redis.service.impl;

import com.alan10607.leaf.dto.LikeDTO;
import com.alan10607.redis.constant.LikeKeyType;
import com.alan10607.redis.constant.LikeStatus;
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
    private static final int CONTENT_LIKE_EXPIRE_SEC = 3600;


    private String getKey(String id, int no, LikeKeyType keyType, String userId) {
        return String.format("data:cont:%s:%s:like:%s:%s", id, no, keyType.value, userId);
    }

    private List getKeyList(String id, int no, String userId){
        return Arrays.asList(getKey(id, no, LikeKeyType.NEW, userId),
                getKey(id, no, LikeKeyType.STATIC, userId));
    }

    private String getValue(boolean like){
        return like ? "1" : "0";
    }

    public LikeDTO get(String id, int no, String userId){
        Long luaQueryResult = stringRedisService.execute(getContentLikeScript,
                getKeyList(id, no, userId));

        LikeDTO likeDTO = new LikeDTO(id,
                no,
                userId,
                luaQueryResult);

        if(likeDTO.getLikeKeyType() == null) {
            log.info("Redis contLike not found, id={}, no={}, userId={}", id, no, userId);
        }

        return likeDTO;
    }

    public boolean set(LikeDTO likeDTO) {
        Long isSuccess = stringRedisService.execute(setContentLikeScript,
                getKeyList(likeDTO.getId(), likeDTO.getNo(), likeDTO.getUserId()),
                likeDTO.getLikeNumberString());

        if(isSuccess == 0){
            log.info("Already {}, skip this time, id={}, no={}, userId={}",
                likeDTO.getLikeString(), likeDTO.getId(), likeDTO.getNo(), likeDTO.getUserId());
        }else if(isSuccess == -1) {
            throw new RuntimeException(String.format(
                "Update to %s failed because redis key not found, id=%s, no=%s, userId=%s",
                likeDTO.getLikeString(), likeDTO.getId(), likeDTO.getNo(), likeDTO.getUserId()));
        }

        return isSuccess == 1;
    }

    public void setWithKeyType(LikeDTO likeDTO) {
        stringRedisService.setString(
            getKey(likeDTO.getId(), likeDTO.getNo(), likeDTO.getLikeKeyType(), likeDTO.getUserId()),
            likeDTO.getLikeNumberString());
    }

    public void expire(String id, int no, String userId, LikeKeyType LikeKeyType) {
        stringRedisService.expire(
            getKey(id, no, LikeKeyType, userId),
            CONTENT_LIKE_EXPIRE_SEC);
    }




}
