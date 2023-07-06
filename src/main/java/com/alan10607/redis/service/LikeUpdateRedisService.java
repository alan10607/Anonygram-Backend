package com.alan10607.redis.service;

import com.alan10607.leaf.dto.LikeDTO;
import com.alan10607.redis.service.base.SetBaseRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class LikeUpdateRedisService {
    private final SetBaseRedisService setBaseRedisService;
    private static final String KEY = "data:likeUpdate";
    private static final String KEY_BATCH = "data:likeUpdate:batch";

    private String getValue(String id, int no, String userId){
        return String.format("%s:%s:%s", id, no, userId);
    }

    public List<LikeDTO> get() {
         return parseValue(setBaseRedisService.get(KEY));
    }

    public void set(LikeDTO likeDTO) {
        setBaseRedisService.set(KEY, getValue(likeDTO.getId(), likeDTO.getNo(), likeDTO.getUserId()));
    }

    public void set(List<LikeDTO> likeList) {
        String[] values = likeList.stream()
                .map(likeDTO -> getValue(likeDTO.getId(), likeDTO.getNo(), likeDTO.getUserId()))
                .toArray(size -> new String[size]);
        setBaseRedisService.set(KEY, values);
    }

    public void renameToBatch() {
        setBaseRedisService.rename(KEY, KEY_BATCH);
    }

    public List<LikeDTO> getBatch() {
        return parseValue(setBaseRedisService.get(KEY_BATCH));
    }

    private List<LikeDTO> parseValue(Set<String> keyList){
        return keyList.stream()
                .map(str -> str.split(":"))
                .map(split -> new LikeDTO(split[0], Integer.parseInt(split[1]), split[2]))
                .collect(Collectors.toList());
    }

}
