package com.alan10607.redis.controller;

import com.alan10607.leaf.dto.SimpleDTO;
import com.alan10607.redis.service.IdRedisService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/redis/id")
@AllArgsConstructor
public class IdRedisController {
    private final IdRedisService idRedisService;

    @GetMapping
    public List<String> get(){
        return idRedisService.get();
    }

    @PostMapping
    public void set(@RequestBody SimpleDTO simpleDTO){
        idRedisService.set((List<String>) simpleDTO.getList());
    }

    @PatchMapping("/{id}/top")
    public void updateScoreToTop(@PathVariable String id){
        idRedisService.updateScoreToTop(id);
    }


}