package com.alan10607.redis.controller;

import com.alan10607.ag.dto.SimpleDTO;
import com.alan10607.redis.dto.LikeDTO;
import com.alan10607.redis.service.UpdateLikeRedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/redis/likeUpdate")
@AllArgsConstructor
@Tag(name = "Redis Control")
public class LikeUpdateRedisController {
    private final UpdateLikeRedisService updateLikeRedisService;

    @GetMapping
    @Operation(summary = "Get all update lists that need to synchronize data to DB")
    public List<LikeDTO> get(){
        return updateLikeRedisService.get();
    }

    @GetMapping("/batch")
    @Operation(summary = "Get all batching lists that need to synchronize data to DB")
    public List<LikeDTO> getBatch(){
        return updateLikeRedisService.getBatch();
    }

    @GetMapping("/exist/{id}/{no}/{userId}")
    @Operation(summary = "Check is the data in update or batching lists")
    public boolean existOrBatchExist(@PathVariable("id") String id,
                                     @PathVariable("no") int no,
                                     @PathVariable("userId") String userId) {
        return updateLikeRedisService.existOrBatchExist(id, no, userId);
    }

    @PostMapping
    @Operation(summary = "Set a update like data to Redis")
    public void set(@RequestBody @Validated({ SimpleDTO.ListGroup.class }) SimpleDTO simpleDTO){
        List<LikeDTO> likeDTOList = simpleDTO.getList().stream()
                .map(LikeDTO::toDTO)
                .collect(Collectors.toList());

        updateLikeRedisService.set(likeDTOList);
    }

    @PostMapping("/renameToBatch")
    @Operation(summary = "Move all lists from update to batching")
    public void renameToBatch(){
        updateLikeRedisService.renameToBatch();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "Delete all lists from batching")
    public void deleteBatch(){
        updateLikeRedisService.deleteBatch();
    }
}