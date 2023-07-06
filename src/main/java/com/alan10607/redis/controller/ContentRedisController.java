package com.alan10607.redis.controller;

import com.alan10607.leaf.dto.ContentDTO;
import com.alan10607.redis.service.ContentRedisService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/redis/content")
@AllArgsConstructor
public class ContentRedisController {
    private final ContentRedisService contentRedisService;

    @GetMapping("/{id}/{no}")
    public ContentDTO get(@PathVariable("id") String id,
                          @PathVariable("no") int no) {
        return contentRedisService.get(id, no);
    }

    @PostMapping
    public void set(@RequestBody ContentDTO contentDTO){
        contentRedisService.set(contentDTO);
    }

    @PatchMapping("/{id}/{no}/expire")
    public void expire(@PathVariable("id") String id,
                       @PathVariable("no") int no){
        contentRedisService.expire(id, no);
    }


}