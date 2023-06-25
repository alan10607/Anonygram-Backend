package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.constant.StatusType;
import com.alan10607.leaf.dao.ArticleDAO;
import com.alan10607.leaf.dao.ContentDAO;
import com.alan10607.leaf.dto.ArticleDTO;
import com.alan10607.leaf.model.Article;
import com.alan10607.leaf.model.Content;
import com.alan10607.leaf.service.ArticleServiceNew;
import com.alan10607.leaf.util.TimeUtil;
import com.alan10607.redis.service.impl.ArticleRedisService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Slf4j
public class ArticleServiceImplNew implements ArticleServiceNew {
    private final ArticleDAO articleDAO;
    private ContentDAO contentDAO;
    private ArticleRedisService articleRedisService;

    public ArticleDTO get(String id) {
        ArticleDTO articleDTO = articleRedisService.get(id);
        if(Strings.isBlank(articleDTO.getId())){
            pullToRedis(id);
            articleDTO = articleRedisService.get(id);
        }
        return articleFilter(articleDTO);
    }

    private void pullToRedis(String id) {
        Article article = articleDAO.findById(id).orElseGet(() -> {
            log.error("Pull Article failed, id={}, will put empty data to redis", id);
            Article unknownArticle = new Article();
            unknownArticle.setStatus(StatusType.UNKNOWN);
            return unknownArticle;
        });

        ArticleDTO articleDTO = new ArticleDTO(article.getId(),
                    article.getTitle(),
                    article.getContNum(),
                    article.getStatus(),
                    article.getUpdateDate(),
                    article.getCreateDate());

        articleRedisService.set(articleDTO);
        log.info("Pull art to redis succeed, id={}", id);
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

    private ArticleDTO findArticle(String id) {
        Article article = articleDAO.findById(id)
                .orElseThrow(() -> new IllegalStateException("Article not found"));

        return new ArticleDTO(article.getId(),
                article.getTitle(),
                article.getContNum(),
                article.getStatus(),
                article.getUpdateDate(),
                article.getCreateDate());
    }

    @AfterDeleteRedis
    public void create(ArticleDTO articleDTO) {
        articleDAO.findById(articleDTO.getId()).ifPresent((a) -> {
            throw new IllegalStateException("Article id already exist");
        });

        Article article = new Article(articleDTO.getId(),
                articleDTO.getTitle(),
                1,
                StatusType.NEW,
                articleDTO.getCreateDate(),
                articleDTO.getCreateDate());

        articleDAO.save(article);
    }

    @AfterDeleteRedis
    public void delete(String id, String userId) {
        updateArticleStatus(id, userId, StatusType.DELETED);
    }

    @AfterDeleteRedis
    public void updateArticleContNumIncrease(String id) {
        articleDAO.incrContNum(id);
    }

    @AfterDeleteRedis
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
    }



    @Transactional
    public void createArtAndCont(String id, int no, String title, String author, String word, LocalDateTime createAndUpdateTime) {
//        create(id, title, createAndUpdateTime);

//        contentDAO.findByIdAndNo(id, no).ifPresent((c) -> {
//            throw new IllegalStateException("Content id already exist");
//        });
//
//
//
//        Content content = new Content(id,
//                no,
//                author,
//                word,
//                0L,
//                StatusType.NEW,
//                createAndUpdateTime,
//                createAndUpdateTime);
//
//        txnService.createArtAndContTxn(article, content);
    }

}