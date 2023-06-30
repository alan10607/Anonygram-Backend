package com.alan10607.redis.controller;

import com.alan10607.leaf.dto.ArticleDTO;
import com.alan10607.leaf.dto.SimpleDTO;
import com.alan10607.redis.service.impl.IdRedisService;
import com.alan10607.redis.service.impl.IdStrRedisService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/redis/idStr")
@AllArgsConstructor
public class IdStrRedisController {
    private final IdStrRedisService idStrRedisService;

    @GetMapping
    public Object get(){
        return idStrRedisService.get();
    }

    @PostMapping
    public void set(@RequestBody SimpleDTO simpleDTO){
        idStrRedisService.set(simpleDTO.getString());
    }

}