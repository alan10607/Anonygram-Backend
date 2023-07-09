package com.alan10607.redis.controller;

import com.alan10607.redis.dto.ArticleDTO;
import com.alan10607.redis.service.ArticleRedisService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/redis/article")
@AllArgsConstructor
@Tag(name = "會員")
public class ArticleRedisController {
    private final ArticleRedisService articleRedisService;

    @Operation(summary = "取得所有會員", description = "取得所有會員資料，每次上限 1000 筆")
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