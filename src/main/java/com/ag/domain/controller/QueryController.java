package com.ag.domain.controller;

import com.ag.domain.dto.UserDTO;
import com.ag.domain.model.Article;
import com.ag.domain.model.ForumUser;
import com.ag.domain.service.QueryArticleService;
import com.ag.domain.service.UserService;
import com.ag.domain.service.base.QueryService;
import com.ag.domain.util.PojoFiledUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "Query Article")
@RequestMapping(path = "/query")
public class QueryController {
    private final QueryArticleService queryArticleService;

    @PostMapping("/{key}")
    @Operation(summary = "Query article")
    public List<Article> get(@PathVariable("key") String key) {
        return queryArticleService.search("articleId", key);
    }

}