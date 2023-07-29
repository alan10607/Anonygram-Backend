package com.alan10607.ag.service.forum;

import com.alan10607.ag.constant.StatusType;
import com.alan10607.ag.dao.ArticleDAO;
import com.alan10607.ag.dao.ContentDAO;
import com.alan10607.ag.dto.ArticleDTO;
import com.alan10607.ag.exception.AnonygramIllegalStateException;
import com.alan10607.ag.model.Article;
import com.alan10607.ag.model.Content;
import com.alan10607.ag.util.TimeUtil;
import com.alan10607.ag.service.redis.ArticleRedisService;
import com.alan10607.ag.service.redis.LockRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ArticleService {
    private final ContentService contentService;
    private final ArticleRedisService articleRedisService;
    private final LockRedisService lockRedisService;
    private final ArticleDAO articleDAO;
    private final ContentDAO contentDAO;

    public ArticleDTO get(String id) {
        ArticleDTO articleDTO = articleRedisService.get(id);
        if(Strings.isBlank(articleDTO.getId())){
            lockRedisService.lockByArticle(id, () -> { pullToRedis(id); });
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
            .orElseGet(() -> {//need test
                log.error("Pull Article failed, id={}, will put empty data to redis", id);
                return new ArticleDTO(id, StatusType.UNKNOWN);
            });

        articleRedisService.set(articleDTO);
        articleRedisService.expire(id);
        log.info("Pull article to redis succeed, id={}", id);
    }

    private ArticleDTO articleFilter(ArticleDTO articleDTO) {
        switch(articleDTO.getStatus()){
            case UNKNOWN :
                throw new AnonygramIllegalStateException("Article not found, id={}", articleDTO.getId());
            case DELETED :
                return new ArticleDTO(articleDTO.getId(), StatusType.DELETED);
            default :
                return articleDTO;
        }
    }

    public void create(ArticleDTO articleDTO) {
        articleDAO.findById(articleDTO.getId()).ifPresent((a) -> {
            throw new AnonygramIllegalStateException("Article already exist, id={}", articleDTO.getId());
        });

        Article article = new Article(articleDTO.getId(),
                articleDTO.getTitle(),
                StatusType.NORMAL,
                articleDTO.getCreateDate(),
                articleDTO.getCreateDate());

        articleDAO.save(article);
        articleRedisService.delete(articleDTO.getId());
    }

    public void updateArticleStatus(String id, String userId, StatusType status) {
        Article article = articleDAO.findById(id)
                .orElseThrow(() -> new AnonygramIllegalStateException("Article not found"));

        Content content = contentDAO.findByIdAndNo(id, 0)
                .orElseThrow(() -> new AnonygramIllegalStateException("Content no 0 not found"));

        if(!userId.equals(content.getAuthor())) {
            throw new AnonygramIllegalStateException("No authority to modify");
        }

        article.setStatus(status);
        article.setUpdateDate(TimeUtil.now());
        articleDAO.save(article);
        articleRedisService.delete(id);
    }

}