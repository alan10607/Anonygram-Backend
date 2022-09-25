package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.constant.ArtStatusType;
import com.alan10607.leaf.dao.ContentDAO;
import com.alan10607.leaf.dto.ContentDTO;
import com.alan10607.leaf.model.Content;
import com.alan10607.leaf.service.ContentService;
import com.alan10607.leaf.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ContentServiceImpl implements ContentService {
    private final ContentDAO contentDAO;
    private final TimeUtil timeUtil;

    public ContentDTO findContent(ContentDTO contentDTO) {
        Content content = contentDAO.findById(contentDTO.getId())
                .orElseThrow(() -> new IllegalStateException("Content id not found"));

        return new ContentDTO(content.getId(),
                content.getAuthor(),
                content.getLikes(),
                content.getWord(),
                content.getStatus(),
                content.getCreateDate());
    }

    public void createContent(ContentDTO contentDTO) {
        contentDAO.findById(contentDTO.getId())
                .ifPresent((a) -> { throw new IllegalStateException("Content id already exist"); });

        contentDAO.save(new Content(contentDTO.getId(),
                contentDTO.getAuthor(),
                contentDTO.getLike(),
                contentDTO.getWord(),
                ArtStatusType.NORMAL.name(),
                timeUtil.now(),
                0L,
                "parentId"));
    }

    public void updateContentLike(ContentDTO contentDTO) {
        Content content = contentDAO.findById(contentDTO.getId())
                .orElseThrow(() -> new IllegalStateException("Content id not found"));

        content.setLikes(contentDTO.getLike());
        contentDAO.save(content);
    }

    public void deleteContent(ContentDTO contentDTO) {
        Content content = contentDAO.findById(contentDTO.getId())
                .orElseThrow(() -> new IllegalStateException("Content id not found"));

        contentDAO.delete(content);
    }
}
