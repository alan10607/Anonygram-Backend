package com.alan10607.redis.service;

import com.alan10607.redis.dto.LikeDTO;
import com.alan10607.redis.constant.LikeKeyType;
import com.alan10607.redis.service.base.StringBaseRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class LikeRedisService {
    private final StringBaseRedisService stringBaseRedisService;
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
        Long luaQueryResult = stringBaseRedisService.execute(getContentLikeScript,
                getKeyList(id, no, userId));

        LikeDTO likeDTO = new LikeDTO(id,
                no,
                userId,
                luaQueryResult);

        if(likeDTO.getLikeKeyType() == LikeKeyType.UNKNOWN) {
            log.info("Redis contLike not found, id={}, no={}, userId={}", id, no, userId);
        }

        return likeDTO;
    }

    public boolean set(LikeDTO likeDTO) {
        Long isSuccess = stringBaseRedisService.execute(setContentLikeScript,
                getKeyList(likeDTO.getId(), likeDTO.getNo(), likeDTO.getUserId()),
                likeDTO.toLikeNumberString());

        if(isSuccess == 0){
            log.info("Already {}, skip this time, id={}, no={}, userId={}",
                likeDTO.toLikeString(), likeDTO.getId(), likeDTO.getNo(), likeDTO.getUserId());
        }else if(isSuccess == -1) {
            throw new RuntimeException(String.format(
                "Update to %s failed because redis key not found, id=%s, no=%s, userId=%s",
                likeDTO.toLikeString(), likeDTO.getId(), likeDTO.getNo(), likeDTO.getUserId()));
        }

        return isSuccess == 1;
    }

    public void setWithKeyType(LikeDTO likeDTO) {
        stringBaseRedisService.set(
            getKey(likeDTO.getId(), likeDTO.getNo(), likeDTO.getLikeKeyType(), likeDTO.getUserId()),
            likeDTO.toLikeNumberString());
    }

    public void expire(String id, int no, String userId, LikeKeyType LikeKeyType) {
        stringBaseRedisService.expire(
            getKey(id, no, LikeKeyType, userId),
            CONTENT_LIKE_EXPIRE_SEC);
    }


}
