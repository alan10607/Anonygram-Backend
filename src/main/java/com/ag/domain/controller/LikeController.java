package com.ag.domain.controller;

import com.ag.domain.dto.LikeDTO;
import com.ag.domain.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Tag(name = "Article Like")
@RequestMapping(path = "like")
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/{articleId}/{no}/{userId}")
    @Operation(summary = "Get article like state")
    public LikeDTO get(@PathVariable("articleId") String articleId,
                       @PathVariable("no") Integer no,
                       @PathVariable("userId") String userId) {
        boolean isLike = likeService.get(articleId, no, userId) != null;
        return new LikeDTO(isLike);
    }

    @PostMapping("/{articleId}/{no}/{userId}")
    @Operation(summary = "Like a article")
    public void create(@PathVariable("articleId") String articleId,
                       @PathVariable("no") Integer no,
                       @PathVariable("userId") String userId) {
        likeService.create(articleId, no, userId);
    }

    @DeleteMapping("/{articleId}/{no}/{userId}")
    @Operation(summary = "Dislike a article")
    public void delete(@PathVariable("articleId") String articleId,
                       @PathVariable("no") Integer no,
                       @PathVariable("userId") String userId) {
        likeService.delete(articleId, no, userId);
    }


}