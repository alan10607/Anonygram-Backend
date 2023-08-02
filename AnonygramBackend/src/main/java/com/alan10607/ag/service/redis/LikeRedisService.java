package com.alan10607.ag.service.redis;

import com.alan10607.ag.exception.RedisIllegalStateException;
import com.alan10607.ag.service.redis.base.StringBaseRedisService;
import com.alan10607.ag.constant.RedisKey;
import com.alan10607.ag.dto.LikeDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@AllArgsConstructor
@Slf4j
public class LikeRedisService {
    private final StringBaseRedisService stringBaseRedisService;
    private final DefaultRedisScript setContentLikeScript;
    private static final int CONTENT_LIKE_EXPIRE_SEC = 3600;

    private String getKey(String id, int no, String userId) {
        return String.format(RedisKey.LIKE, id, no, userId);
    }

    public Boolean get(String id, int no, String userId){
        String res = stringBaseRedisService.get(getKey(id, no, userId));
        if("1".equals(res)) return true;
        if("0".equals(res)) return false;
        return null;
    }

    public boolean set(LikeDTO likeDTO) {
        Long isSuccess = stringBaseRedisService.execute(setContentLikeScript,
                Arrays.asList(getKey(likeDTO.getId(), likeDTO.getNo(), likeDTO.getUserId())),
                likeDTO.toLikeNumberString());

        if(isSuccess == 0){
            log.info("Already {}, skip this time, id={}, no={}, userId={}",
                likeDTO.toLikeString(), likeDTO.getId(), likeDTO.getNo(), likeDTO.getUserId());
        }else if(isSuccess == -1) {
            throw new RedisIllegalStateException(
                "Update to {} failed because redis key not found, id={}, no={}, userId={}",
                likeDTO.toLikeString(), likeDTO.getId(), likeDTO.getNo(), likeDTO.getUserId());
        }

        return isSuccess == 1;
    }

    public void expire(String id, int no, String userId) {
        stringBaseRedisService.expire(getKey(id, no, userId), CONTENT_LIKE_EXPIRE_SEC);
    }

}