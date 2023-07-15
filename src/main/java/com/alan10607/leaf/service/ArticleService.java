package com.alan10607.leaf.service;

import com.alan10607.leaf.constant.StatusType;
import com.alan10607.leaf.dao.ArticleDAO;
import com.alan10607.leaf.dao.ContentDAO;
import com.alan10607.redis.dto.ArticleDTO;
import com.alan10607.leaf.model.Article;
import com.alan10607.leaf.model.Content;
import com.alan10607.leaf.util.TimeUtil;
import com.alan10607.redis.service.ArticleRedisService;
import com.alan10607.redis.service.LockRedisService;
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
                throw new IllegalStateException(
                        String.format("Article not found, id: %s", articleDTO.getId()));
            case DELETED :
                return new ArticleDTO(articleDTO.getId(), StatusType.DELETED);
            default :
                return articleDTO;
        }
    }

    public void create(ArticleDTO articleDTO) {
        articleDAO.findById(articleDTO.getId()).ifPresent((a) -> {
            throw new IllegalStateException(String.format("Article already exist, id: %s", articleDTO.getId()));
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
                .orElseThrow(() -> new IllegalStateException("Article not found"));

        Content content = contentDAO.findByIdAndNo(id, 0)
                .orElseThrow(() -> new IllegalStateException("Content no 0 not found"));

        if(!userId.equals(content.getAuthor()))
            throw new IllegalStateException("No authority to modify");

        article.setStatus(status);
        article.setUpdateDate(TimeUtil.now());
        articleDAO.save(article);
        articleRedisService.delete(id);
    }

}