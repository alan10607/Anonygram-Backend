package com.alan10607.redis.controller;

import com.alan10607.redis.dto.LikeDTO;
import com.alan10607.redis.service.LikeRedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/redis/like")
@AllArgsConstructor
@Tag(name = "Like Redis")
public class LikeRedisController {
    private final LikeRedisService likeRedisService;

    @GetMapping("/{id}/{no}/{userId}")
    @Operation(summary = "Get a content like from Redis")
    public Boolean get(@PathVariable("id") String id,
                       @PathVariable("no") int no,
                       @PathVariable("userId") String userId) {
        return likeRedisService.get(id, no, userId);
    }

    @PostMapping()
    @Operation(summary = "Save a content like to Redis")
    public boolean set(@RequestBody @Valid LikeDTO likeDTO) {
        return likeRedisService.set(likeDTO);
    }

    @PatchMapping("/expire/{id}/{no}/{userId}")
    @Operation(summary = "Reset a content like Redis expiration")
    public void expire(@PathVariable("id") String id,
                       @PathVariable("no") int no,
                       @PathVariable("userId") String userId) {
        likeRedisService.expire(id, no, userId);
    }
}