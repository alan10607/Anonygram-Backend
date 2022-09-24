package com.alan10607.leaf.service;

import com.alan10607.leaf.dto.ArticleDTO;
import com.alan10607.leaf.dto.ContentDTO;

import java.util.List;

public interface ContentService {
    ContentDTO findContent(ContentDTO contentDTO);
    void createContent(ContentDTO contentDTO);
    void updateContentLike(ContentDTO contentDTO);
    void deleteContent(ContentDTO contentDTO);
}