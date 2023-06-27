package com.alan10607.redis.controller;

import com.alan10607.leaf.dto.ContentDTO;
import com.alan10607.leaf.dto.SimpleDTO;
import com.alan10607.redis.service.impl.ContentRedisService;
import com.alan10607.redis.service.impl.IdRedisService;
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

    @PostMapping("/Bulk")
    public void setBulk(@RequestBody SimpleDTO simpleDTO){
        idRedisService.set((List<String>) simpleDTO.getList());
    }

    @PostMapping
    public void set(@RequestBody SimpleDTO simpleDTO){
        idRedisService.set(simpleDTO.getString());
    }

    @PatchMapping("/{id}/top")
    public void updateScoreToTop(@PathVariable String id){
        idRedisService.updateScoreToTop(id);
    }


}