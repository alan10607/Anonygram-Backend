package com.alan10607.redis.controller;

import com.alan10607.leaf.dto.SimpleDTO;
import com.alan10607.redis.dto.LikeDTO;
import com.alan10607.redis.service.LikeUpdateRedisService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/redis/likeUpdate")
@AllArgsConstructor
public class LikeUpdateRedisController {
    private final LikeUpdateRedisService likeUpdateRedisService;

    @GetMapping
    public List<LikeDTO> get(){
        return likeUpdateRedisService.get();
    }

    @GetMapping("/batch")
    public List<LikeDTO> getBatch(){
        return likeUpdateRedisService.getBatch();
    }

    @GetMapping("/exist/{id}/{no}/{userId}")
    public boolean existOrBatchExist(@PathVariable("id") String id,
                                     @PathVariable("no") int no,
                                     @PathVariable("userId") String userId) {
        return likeUpdateRedisService.existOrBatchExist(id, no, userId);
    }

    @PostMapping
    public void set(@RequestBody @Validated({ SimpleDTO.ListGroup.class }) SimpleDTO simpleDTO){
        List<LikeDTO> likeDTOList = simpleDTO.getList().stream()
                .map(LikeDTO::toDTO)
                .collect(Collectors.toList());

        likeUpdateRedisService.set(likeDTOList);
    }

    @PostMapping("/renameToBatch")
    public void renameToBatch(){
        likeUpdateRedisService.renameToBatch();
    }

    @DeleteMapping("/batch")
    public void deleteBatch(){
        likeUpdateRedisService.deleteBatch();
    }
}