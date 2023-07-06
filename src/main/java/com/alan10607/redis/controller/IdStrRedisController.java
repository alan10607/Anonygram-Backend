package com.alan10607.redis.controller;

import com.alan10607.leaf.dto.SimpleDTO;
import com.alan10607.redis.service.IdStrRedisService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/redis/idStr")
@AllArgsConstructor
public class IdStrRedisController {
    private final IdStrRedisService idStrRedisService;

    @GetMapping
    public String get(){
        return idStrRedisService.get();
    }

    @PostMapping
    public void set(@RequestBody SimpleDTO simpleDTO){
        idStrRedisService.set(simpleDTO.getString());
    }

}