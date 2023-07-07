package com.alan10607.redis.controller;

import com.alan10607.redis.constant.LikeKeyType;
import com.alan10607.redis.dto.LikeDTO;
import com.alan10607.redis.service.LikeRedisService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/redis/like")
@AllArgsConstructor
public class LikeRedisController {
    private final LikeRedisService likeRedisService;

    @GetMapping("/{id}/{no}/{userId}")
    public LikeDTO get(@PathVariable("id") String id,
                       @PathVariable("no") int no,
                       @PathVariable("userId") String userId) {
        return likeRedisService.get(id, no, userId);
    }

    @PostMapping()
    public boolean set(@RequestBody @Valid LikeDTO likeDTO) {
        return likeRedisService.set(likeDTO);
    }

    @PostMapping("/keyType")
    public void setWithKeyType(@RequestBody @Validated({LikeDTO.KeyTypeGroup.class}) LikeDTO likeDTO) {
        likeRedisService.setWithKeyType(likeDTO);
    }

    @PatchMapping("/expire/{id}/{no}/{userId}/{keyType}")
    public void expire(@PathVariable("id") String id,
                       @PathVariable("no") int no,
                       @PathVariable("userId") String userId,
                       @PathVariable("keyType") String keyType) {
        likeRedisService.expire(id, no, userId, LikeKeyType.valueOf(keyType));
    }
}