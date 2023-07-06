package com.alan10607.redis.service.impl;

import com.alan10607.leaf.dto.LikeDTO;
import com.alan10607.redis.service.SetRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ContLikeUpdateRedisService {
    private final SetRedisService setRedisService;
    private static final String KEY = "data:likeUpdate";
    private static final String KEY_BATCH = "data:likeUpdate:batch";

    private String getValue(String id, int no, String userId){
        return String.format("%s:%s:%s", id, no, userId);
    }

    public List<LikeDTO> get() {
         return parseValue(setRedisService.getSet(KEY));
    }

    public void set(LikeDTO likeDTO) {
        setRedisService.setSet(KEY, getValue(likeDTO.getId(), likeDTO.getNo(), likeDTO.getUserId()));
    }

    public void set(List<LikeDTO> likeList) {
        String[] values = likeList.stream()
                .map(likeDTO -> getValue(likeDTO.getId(), likeDTO.getNo(), likeDTO.getUserId()))
                .toArray(size -> new String[size]);
        setRedisService.setSet(KEY, values);
    }

    public void renameToBatch() {
        setRedisService.rename(KEY, KEY_BATCH);
    }

    public List<LikeDTO> getBatch() {
        return parseValue(setRedisService.getSet(KEY_BATCH));
    }

    private List<LikeDTO> parseValue(Set<String> keyList){
        return keyList.stream()
                .map(str -> str.split(":"))
                .map(split -> new LikeDTO(split[0], Integer.parseInt(split[1]), split[2]))
                .collect(Collectors.toList());
    }

}
