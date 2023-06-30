package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.constant.StatusType;
import com.alan10607.leaf.dao.ArticleDAO;
import com.alan10607.leaf.dao.ContentDAO;
import com.alan10607.leaf.dto.ArticleDTO;
import com.alan10607.leaf.dto.ContentDTO;
import com.alan10607.leaf.model.Article;
import com.alan10607.leaf.model.Content;
import com.alan10607.leaf.service.ArticleServiceNew;
import com.alan10607.leaf.service.ContentServiceNew;
import com.alan10607.leaf.util.TimeUtil;
import com.alan10607.redis.service.impl.ArticleRedisService;
import com.alan10607.redis.service.impl.ContentRedisService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class ContentServiceImplNew implements ContentServiceNew {
    private final ContentDAO contentDAO;
    private final ArticleRedisService articleRedisService;
    private final ContentRedisService contentRedisService;

    public ContentDTO get(String id, int no) {
        ContentDTO contentDTO = contentRedisService.get(id, no);
        if(Strings.isBlank(contentDTO.getId()) || contentDTO.getNo() == null){
            pullToRedis(id, no);
            contentDTO = contentRedisService.get(id, no);
        }
        contentRedisService.expire(id, no);

        return contentFilter(contentDTO);
    }

    private void pullToRedis(String id, int no) {
        ContentDTO contentDTO = contentDAO.findByIdAndNo(id, no)
            .map(content -> new ContentDTO(content.getId(),
                content.getNo(),
                content.getAuthor(),
                content.getWord(),
                content.getLikes(),
                content.getStatus(),
                content.getCreateDate(),
                content.getUpdateDate()))
            .orElseGet(() -> {
                log.error("Pull Content failed, id={}, no={}, put empty data to redis", id, no);
                return new ContentDTO(id, no, StatusType.UNKNOWN);
            });

        contentRedisService.set(contentDTO);
        contentRedisService.expire(id, no);
        log.info("Pull Content to redis succeed, id={}", id);
    }

    private ContentDTO contentFilter(ContentDTO contentDTO) {
        switch(contentDTO.getStatus()){
            case UNKNOWN :
                throw new IllegalStateException(
                        String.format("Content not found, id: %s, no: %s", contentDTO.getId(), contentDTO.getNo()));
            case DELETED :
                return new ContentDTO(contentDTO.getId(), contentDTO.getNo(), StatusType.DELETED);
            default :
                return contentDTO;
        }
    }

    public int create(ContentDTO contentDTO) {
        contentDAO.findByIdAndNo(contentDTO.getId(), contentDTO.getNo()).ifPresent((c) -> {
            throw new IllegalStateException("Content id already exist");
        });

        Content content = new Content(contentDTO.getId(),
                contentDTO.getAuthor(),
                contentDTO.getWord(),
                0L,
                StatusType.NORMAL,
                contentDTO.getCreateDate(),
                contentDTO.getCreateDate());

        content = contentDAO.save(content);
        contentRedisService.delete(contentDTO.getId(), contentDTO.getNo());
        articleRedisService.delete(contentDTO.getId());
        return content.getNo();
    }

    public void updateContentStatus(String id, int no, String userId, StatusType status) {
        Content content = contentDAO.findByIdAndNo(id, no)
                .orElseThrow(() -> new IllegalStateException("Content not found"));

        if(!userId.equals(content.getAuthor()))
            throw new IllegalStateException("No authority to modify");

        content.setStatus(status);
        content.setUpdateDate(TimeUtil.now());
        contentDAO.save(content);
        contentRedisService.delete(id, no);
    }

    public Integer getContentSizeById(String id){
        return contentDAO.countById(id);
    }

    public void updateContentLikesFromRedis(String id, int no, long addNum) {
        contentRedisService.increaseLikes(id, no, addNum);
    }
}