package com.alan10607.redis.controller;

import com.alan10607.leaf.dto.LikeDTO;
import com.alan10607.leaf.dto.SimpleDTO;
import com.alan10607.redis.service.LikeRedisService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/redis/contLike")
@AllArgsConstructor
public class LikeRedisController {
    private final LikeRedisService likeRedisService;

    @GetMapping("/{id}/{no}/{userId}")
    public LikeDTO get(@PathVariable("id") String id,
                       @PathVariable("no") int no,
                       @PathVariable("userId") String userId){
        return likeRedisService.get(id, no, userId);
    }

    @PutMapping()
    public boolean setLike(@RequestBody LikeDTO likeDTO){
        return likeRedisService.set(id, no, userId, LikeRedisService.LikeStatus.LIKE);
    }

    @PutMapping("/{id}/{no}/{userId}/dislike")
    public boolean setDislike(@PathVariable("id") String id,
                        @PathVariable("no") int no,
                        @PathVariable("userId") String userId){
        return likeRedisService.set(id, no, userId, LikeRedisService.LikeStatus.DISLIKE);
    }

    @PutMapping("/{id}/{no}/{userId}")
    public void set(@PathVariable("id") String id,
                    @PathVariable("no") int no,
                    @PathVariable("userId") String userId,
                    @RequestBody SimpleDTO simpleDTO){
        List<String> dataList = (List<String>) simpleDTO.getList();
        String keyType = dataList.get(0);
        String likeStatus = dataList.get(1);

        likeRedisService.set(id,
                no,
                LikeRedisService.KeyType.valueOf(keyType),
                userId,
                LikeRedisService.LikeStatus.valueOf(likeStatus));
    }


}