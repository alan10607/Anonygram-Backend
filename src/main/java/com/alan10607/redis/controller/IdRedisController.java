package com.alan10607.redis.controller;

import com.alan10607.leaf.dto.SimpleDTO;
import com.alan10607.redis.service.IdRedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/redis/id")
@AllArgsConstructor
@Tag(name = "Redis Control")
public class IdRedisController {
    private final IdRedisService idRedisService;

    @GetMapping
    @Operation(summary = "Get all ids from Redis")
    public List<String> get(){
        return idRedisService.get();
    }

    @PostMapping
    @Operation(summary = "Save some ids to Redis")
    public void set(@RequestBody @Validated({ SimpleDTO.ListGroup.class }) SimpleDTO simpleDTO){
        idRedisService.set((List<String>) simpleDTO.getList());
    }

    @PatchMapping("/top/{id}")
    @Operation(summary = "Move id to the top of list")
    public void updateScoreToTop(@PathVariable String id){
        idRedisService.updateScoreToTop(id);
    }


}