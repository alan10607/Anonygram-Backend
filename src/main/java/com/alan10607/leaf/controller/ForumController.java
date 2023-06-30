package com.alan10607.leaf.controller;

import com.alan10607.leaf.dto.SimpleDTO;
import com.alan10607.leaf.service.impl.ForumService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(path = "/forum")
@AllArgsConstructor
public class ForumController {
    private final ForumService forumService;

    @GetMapping()
    public Object getId(){
        return forumService.getId();
    }

    @GetMapping("/{id}")
    public Object getFirstForums(@PathVariable("id") List<String> idList){
        return forumService.getFirstForums(idList);
    }

    @GetMapping("/{id}/{no}")
    public Object getTopContents(@PathVariable("id") String id,
                                 @PathVariable("no") int no){
        return forumService.getTopContents(id, no);
    }

}