package com.alan10607.ag.service.redis;

import com.alan10607.ag.constant.RedisKey;
import com.alan10607.ag.dto.UserDTO;
import com.alan10607.ag.service.redis.base.HashBaseRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class UserRedisService {
    private final HashBaseRedisService hashBaseRedisService;
    private static final int USER_NAME_EXPIRE_SEC = 3600;

    private String getKey(String userId) {
        return String.format(RedisKey.USER, userId);
    }

    public UserDTO get(String userId){
        Map<String, Object> dataMap = hashBaseRedisService.get(getKey(userId));
        return UserDTO.toDTO(dataMap);
    }

    public void set(UserDTO userDTO) {
        Map<String, Object> dataMap = userDTO.toMap();
        hashBaseRedisService.set(getKey(userDTO.getId()), dataMap);
    }

    public void expire(String id) {
        hashBaseRedisService.expire(getKey(id), USER_NAME_EXPIRE_SEC);
    }

}
