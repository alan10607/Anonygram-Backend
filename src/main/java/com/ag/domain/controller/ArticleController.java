package com.ag.domain.controller;

import com.ag.domain.constant.StatusType;
import com.ag.domain.dto.ArticleDTO;
import com.ag.domain.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "article")
@AllArgsConstructor
@Tag(name = "Anonygram Forum")
public class ArticleController {


    private final ArticleService articleService;


    @GetMapping("/{id}/{no}")
    @Operation(summary = "Get a article")
    public ArticleDTO get(@PathVariable("id") String id,
                          @PathVariable("no") Integer no) {
        return articleService.get(id, no);
    }

    @PostMapping()
    @Operation(summary = "Create a article with first content")
    public ArticleDTO create(@RequestBody ArticleDTO articleDTO) {
        return articleService.create(articleDTO);
    }

    @PostMapping("/{id}")
    @Operation(summary = "Create a content under article")
    public ArticleDTO createContent(@PathVariable("id") String id,
                                    @RequestBody ArticleDTO articleDTO) {
        articleDTO.setId(id);
        return articleService.create(articleDTO);
    }

    @PatchMapping("/{id}/{no}/word")
    @Operation(summary = "To modify a content")
    public ArticleDTO patchWord(@PathVariable("id") String id,
                                @PathVariable("no") int no,
                                @RequestBody ArticleDTO articleDTO) {
        articleDTO.setId(id);
        articleDTO.setNo(no);
        return articleService.patchWord(articleDTO);
    }

    @PatchMapping("/{id}/{no}/like")
    @Operation(summary = "To like a content")
    public ArticleDTO patchLike(@PathVariable("id") String id,
                                @PathVariable("no") int no,
                                @RequestBody ArticleDTO articleDTO) {
        //TODO: need work, replace this to /like/{id}
        return null;
    }

    @DeleteMapping("/{id}/{no}")
    @Operation(summary = "Delete a content. If delete first content, will also delete its article")
    public ArticleDTO patchStatusToDelete(@PathVariable("id") String id,
                                          @PathVariable("no") int no) {
        ArticleDTO articleDTO = new ArticleDTO(id, no);
        articleDTO.setStatus(StatusType.DELETED);
        return articleService.patchStatus(articleDTO);
    }


}