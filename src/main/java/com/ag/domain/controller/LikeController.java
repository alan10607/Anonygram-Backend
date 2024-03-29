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

    @GetMapping("/{articleId}/{no}")
    @Operation(summary = "Get article like state")
    public LikeDTO get(@PathVariable("articleId") String articleId,
                       @PathVariable("no") Integer no) {
        boolean isLike = likeService.get(articleId, no) != null;
        return new LikeDTO(isLike);
    }

    @PostMapping("/{articleId}/{no}")
    @Operation(summary = "Like a article")
    public void create(@PathVariable("articleId") String articleId,
                       @PathVariable("no") Integer no) {
        likeService.create(articleId, no);
    }

    @DeleteMapping("/{articleId}/{no}")
    @Operation(summary = "Dislike a article")
    public void delete(@PathVariable("articleId") String articleId,
                       @PathVariable("no") Integer no) {
        likeService.delete(articleId, no);
    }


}