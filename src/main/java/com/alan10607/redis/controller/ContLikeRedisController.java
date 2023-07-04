package com.alan10607.redis.controller;

import com.alan10607.leaf.dto.LikeDTO;
import com.alan10607.leaf.dto.SimpleDTO;
import com.alan10607.redis.service.impl.ContLikeRedisService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/redis/contLike")
@AllArgsConstructor
public class ContLikeRedisController {
    private final ContLikeRedisService contLikeRedisService;

    @GetMapping("/{id}/{no}/{userId}")
    public LikeDTO get(@PathVariable("id") String id,
                       @PathVariable("no") int no,
                       @PathVariable("userId") String userId){
        return contLikeRedisService.get(id, no, userId);
    }

    @PutMapping()
    public boolean setLike(@RequestBody LikeDTO likeDTO){
        return contLikeRedisService.set(id, no, userId, ContLikeRedisService.LikeStatus.LIKE);
    }

    @PutMapping("/{id}/{no}/{userId}/dislike")
    public boolean setDislike(@PathVariable("id") String id,
                        @PathVariable("no") int no,
                        @PathVariable("userId") String userId){
        return contLikeRedisService.set(id, no, userId, ContLikeRedisService.LikeStatus.DISLIKE);
    }

    @PutMapping("/{id}/{no}/{userId}")
    public void set(@PathVariable("id") String id,
                    @PathVariable("no") int no,
                    @PathVariable("userId") String userId,
                    @RequestBody SimpleDTO simpleDTO){
        List<String> dataList = (List<String>) simpleDTO.getList();
        String keyType = dataList.get(0);
        String likeStatus = dataList.get(1);

        contLikeRedisService.set(id,
                no,
                ContLikeRedisService.KeyType.valueOf(keyType),
                userId,
                ContLikeRedisService.LikeStatus.valueOf(likeStatus));
    }


}