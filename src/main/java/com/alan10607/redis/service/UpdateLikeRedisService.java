package com.alan10607.redis.service;

import com.alan10607.redis.constant.RedisKey;
import com.alan10607.redis.dto.LikeDTO;
import com.alan10607.redis.service.base.SetBaseRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UpdateLikeRedisService {
    private final SetBaseRedisService setBaseRedisService;
    private final DefaultRedisScript isMemberMultiScript;
    private static final String KEY = RedisKey.UPDATE_LIKE;
    private static final String KEY_BATCH = RedisKey.UPDATE_LIKE_BATCH;

    private String getValue(String id, int no, String userId){
        return String.format("%s:%s:%s", id, no, userId);
    }

    public List<LikeDTO> get() {
         return parseValue(setBaseRedisService.get(KEY));
    }

    public void set(LikeDTO likeDTO) {
        set(Collections.singletonList(likeDTO));
    }

    public void set(List<LikeDTO> likeList) {
        String[] values = likeList.stream()
                .map(likeDTO -> getValue(likeDTO.getId(), likeDTO.getNo(), likeDTO.getUserId()))
                .toArray(String[]::new);
        setBaseRedisService.set(KEY, values);
    }

    public void renameToBatch() {
        setBaseRedisService.rename(KEY, KEY_BATCH);
    }

    public void deleteBatch() {
        setBaseRedisService.delete(KEY_BATCH);
    }

    public List<LikeDTO> getBatch() {
        return parseValue(setBaseRedisService.get(KEY_BATCH));
    }

    public boolean existOrBatchExist(String id, int no, String userId) {
        Long exist = setBaseRedisService.execute(isMemberMultiScript,
                Arrays.asList(KEY, KEY_BATCH),
                getValue(id, no, userId));

        return exist == 1;
    }

    private List<LikeDTO> parseValue(Set<String> keyList){
        return keyList.stream()
                .map(str -> str.split(":"))
                .map(split -> new LikeDTO(split[0], Integer.parseInt(split[1]), split[2]))
                .collect(Collectors.toList());
    }

}
