package com.alan10607.redis.controller;

import com.alan10607.redis.dto.ArticleDTO;
import com.alan10607.redis.service.ArticleRedisService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/redis/article")
@AllArgsConstructor
public class ArticleRedisController {
    private final ArticleRedisService articleRedisService;

    @GetMapping("/{id}")
    public ArticleDTO get(@PathVariable("id") String id){
        return articleRedisService.get(id);
    }

    @PostMapping
    public void set(@RequestBody @Valid ArticleDTO articleDTO){
        articleRedisService.set(articleDTO);
    }

    @PatchMapping("/expire/{id}")
    public void expire(@PathVariable("id") String id){
        articleRedisService.expire(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id){
        articleRedisService.delete(id);
    }


}