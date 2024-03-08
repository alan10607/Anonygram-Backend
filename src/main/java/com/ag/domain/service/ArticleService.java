package com.ag.domain.service;

import com.ag.domain.constant.StatusType;
import com.ag.domain.exception.AgValidationException;
import com.ag.domain.model.Article;
import com.ag.domain.repository.ArticleRepository;
import com.ag.domain.service.base.CrudServiceImpl;
import com.ag.domain.util.PojoFiledUtil;
import com.ag.domain.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class ArticleService extends CrudServiceImpl<Article> {
    private final ArticleRepository articleRepository;
    private final LikeService likeService;
    public static final int MAX_WORD_LENGTH = 5000;
    public static final int MAX_TITLE_LENGTH = 100;

    public Article get(String id, int no) {
        return get(new Article(id, no));
    }

    public Article delete(String id, int no) {
        return delete(new Article(id, no));
    }

    public Article patchWord(Article article) {
        Article patchedArticle = PojoFiledUtil.retainFields(article, "id", "no", "word");
        return patch(patchedArticle);
    }

    public Article patchStatus(Article article) {
        Article patchedArticle = PojoFiledUtil.retainFields(article, "id", "no", "status");
        return patch(patchedArticle);
    }

    @Override
    public Article getImpl(Article article) {
        return articleRepository.findById(article.getId()).orElse(null);
    }

    @Override
    public Article createImpl(Article article) {
        LocalDateTime now = TimeUtil.now();
        article = Article.builder()
                .serial(UUID.randomUUID().toString())
                .no(0)
                .authorId("test")
//                .authorId(AuthUtil.getUserId())//TODO: fix it
                .title(article.getTitle())
                .word(article.getWord())
                .likes(0L)
                .status(StatusType.NORMAL)
                .createdTime(now)
                .updatedTime(now)
                .build();


        return articleRepository.save(article);
    }

    @Override
    public Article updateImpl(Article article) {
        Article existed = articleRepository.findById(article.getSerial()).get();
        article.setSerial(existed.getSerial());
        article.setNo(existed.getNo());
        article.setCreatedTime(existed.getCreatedTime());
        article.setUpdatedTime(TimeUtil.now());

        return articleRepository.save(article);
    }

    @Override
    public Article patchImpl(Article article) {
        return update(article);
    }

    @Override
    public Article deleteImpl(Article article) {
        articleRepository.delete(article);
        return article;
    }

    @Override
    protected void beforeGet(Article article) {
        validateFirstArticleIsNormal(article);
        validateFirstArticleIsNormal(article);
    }

    @Override
    protected void beforeCreate(Article article) {
        if(article.getNo() == 0) {
            article.setNo(0);
        }
        validateTitle(article);
        validateWord(article);
        validateHaveFirstArticle(article);
    }

    @Override
    protected void beforeUpdateAndPatch(Article article) {
        validateHavePermission(article);
        validateWord(article);
        validateStatus(article);
    }

    @Override
    protected void beforeDelete(Article article) {
        validateStatus(article);
        validateHavePermission(article);
    }

    void validateWord(Article article) {
        if (StringUtils.isBlank(article.getWord())) {
            throw new AgValidationException("Word must not be blank", article);
        }//TODO: please update front end
        if (article.getWord().getBytes().length > MAX_WORD_LENGTH) {
            throw new AgValidationException("Word length must in {} bytes", article, MAX_WORD_LENGTH);
        }
    }

    void validateTitle(Article article) {
        if (article.getNo() == 0 && StringUtils.isBlank(article.getTitle())) {
            throw new AgValidationException("Title must not be blank when create", article);
        }
        if (article.getTitle().getBytes().length > MAX_TITLE_LENGTH) {
            throw new AgValidationException("Title length must in {} bytes", article, MAX_TITLE_LENGTH);
        }
    }

    void validateStatus(Article article) {
        if (article.getStatus() != StatusType.NORMAL) {
            throw new AgValidationException("Status is not normal", article);
        }
    }

    void validateHaveFirstArticle(Article article) {
        if (StringUtils.isNotBlank(article.getSerial()) ) {
            articleRepository.findById(article.getId())
                    .orElseThrow(() -> new AgValidationException("First article not found, can't reply"));
        }
    }

    void validateFirstArticleIsNormal(Article article) {
//        Article firstArticle = articleRepository.get(article.getId(), 0);
//        if (firstArticle.getStatus() != StatusType.NORMAL) {
//            throw new AgValidationException("First article's status is not normal", article);
//        }
    }

    void validateHavePermission(Article article) {

    }

}