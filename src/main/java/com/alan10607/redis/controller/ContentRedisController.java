package com.alan10607.redis.controller;

import com.alan10607.leaf.dto.SimpleDTO;
import com.alan10607.redis.dto.ContentDTO;
import com.alan10607.redis.service.ContentRedisService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public void set(@RequestBody @Valid ContentDTO contentDTO){
        contentRedisService.set(contentDTO);
    }

    @PatchMapping("/expire/{id}/{no}")
    public void expire(@PathVariable("id") String id,
                       @PathVariable("no") int no){
        contentRedisService.expire(id, no);
    }

    @PatchMapping("/increaseLikes/{id}/{no}")
    public void increaseLikes(@PathVariable("id") String id,
                       @PathVariable("no") int no,
                       @RequestBody @Validated(SimpleDTO.IntegerGroup.class) SimpleDTO simpleDTO){
        contentRedisService.increaseLikes(id, no, simpleDTO.getInteger());
    }

    @DeleteMapping("/{id}/{no}")
    public void delete(@PathVariable("id") String id,
                       @PathVariable("no") int no) {
        contentRedisService.delete(id, no);
    }


}