package com.alan10607.ag.service.forum;

import com.alan10607.ag.constant.StatusType;
import com.alan10607.ag.dao.ArticleDAO;
import com.alan10607.ag.dao.ContentDAO;
import com.alan10607.ag.dto.ArticleDTO;
import com.alan10607.ag.dto.ContentDTO;
import com.alan10607.ag.exception.AnonygramIllegalStateException;
import com.alan10607.ag.model.Article;
import com.alan10607.ag.service.redis.ArticleRedisService;
import com.alan10607.ag.service.redis.LockRedisService;
import com.alan10607.ag.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ArticleService {
    private final ContentService contentService;
    private final IdService idService;
    private final ArticleRedisService articleRedisService;
    private final LockRedisService lockRedisService;
    private final ArticleDAO articleDAO;
    private final ContentDAO contentDAO;

    public List<ArticleDTO> get(List<String> idList) {
        return idList.stream().map(this::get).collect(Collectors.toList());
    }

    public ArticleDTO get(String id) {
        ArticleDTO articleDTO = articleRedisService.get(id);
        if(StringUtils.isBlank(articleDTO.getId())){
            lockRedisService.lockByArticle(id, () -> pullToRedis(id));
            articleDTO = articleRedisService.get(id);
        }
        articleRedisService.expire(id);

        return articleFilter(articleDTO);
    }

    private void pullToRedis(String id) {
        ArticleDTO articleDTO = articleDAO.findById(id)
            .map(article -> new ArticleDTO(article.getId(),
                article.getTitle(),
                article.getStatus(),
                article.getCreateDate(),
                article.getUpdateDate(),
                contentService.getContentSizeById(id)))
            .orElseGet(() -> {
                log.error("Pull Article failed, id={}, will put empty data to redis", id);
                return new ArticleDTO(id, StatusType.UNKNOWN);
            });

        articleRedisService.set(articleDTO);
        articleRedisService.expire(id);
        log.info("Pull article to redis succeed, id={}", id);
    }

    private ArticleDTO articleFilter(ArticleDTO articleDTO) {
        switch(articleDTO.getStatus()){
            case NORMAL:
                ContentDTO firstContent = contentService.get(articleDTO.getId(), 0);
                articleDTO.setContentList(Collections.singletonList(firstContent));
                return articleDTO;
            case DELETED :
                return new ArticleDTO(articleDTO.getId(), StatusType.DELETED);
            case UNKNOWN :
            default:
                log.info("Article not found, id={}", articleDTO.getId());
                return new ArticleDTO(articleDTO.getId(), StatusType.UNKNOWN);
        }
    }

    @Transactional
    public void create(ArticleDTO articleDTO) {
        articleDAO.findById(articleDTO.getId()).ifPresent((a) -> {
            throw new AnonygramIllegalStateException("Article already exist, id={}", articleDTO.getId());
        });

        prepareCreateValue(articleDTO);

        Article article = new Article(articleDTO.getId(),
                articleDTO.getTitle(),
                StatusType.NORMAL,
                articleDTO.getCreateDate(),
                articleDTO.getCreateDate());

        articleDAO.save(article);
        contentService.create(articleDTO.getContentList().get(0));
        idService.set(articleDTO.getId());
        articleRedisService.delete(articleDTO.getId());
    }

    private void prepareCreateValue(ArticleDTO articleDTO){
        String id = UUID.randomUUID().toString();
        LocalDateTime createDate = TimeUtil.now();

        articleDTO.setId(id);
        articleDTO.setCreateDate(createDate);
        ContentDTO contentDTO = articleDTO.getContentList().get(0);
        contentDTO.setId(id);
        contentDTO.setCreateDate(createDate);
    }

    public void updateStatus(String id, StatusType status) {
        Article article = articleDAO.findById(id)
                .orElseThrow(() -> new AnonygramIllegalStateException("Article not found"));

        article.setStatus(status);
        article.setUpdateDate(TimeUtil.now());
        articleDAO.save(article);
        articleRedisService.delete(id);
    }

}