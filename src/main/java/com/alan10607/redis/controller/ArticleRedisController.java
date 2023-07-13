package com.alan10607.redis.controller;

import com.alan10607.redis.dto.ArticleDTO;
import com.alan10607.redis.service.ArticleRedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/redis/article")
@AllArgsConstructor
@Tag(name = "Article Redis")
public class ArticleRedisController {
    private final ArticleRedisService articleRedisService;

    @GetMapping("/{id}")
    @Operation(summary = "Get a article from Redis", description = "取得所有會員資料，每次上限 1000 筆")
    public ArticleDTO get(@PathVariable("id") String id){
        return articleRedisService.get(id);
    }

    @PostMapping
    @Operation(summary = "Save a article to Redis")
    public void set(@RequestBody @Valid ArticleDTO articleDTO){
        articleRedisService.set(articleDTO);
    }

    @PatchMapping("/expire/{id}")
    @Operation(summary = "Reset a article Redis expiration")
    public void expire(@PathVariable("id") String id){
        articleRedisService.expire(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a article from Redis")
    public void delete(@PathVariable("id") String id){
        articleRedisService.delete(id);
    }


}