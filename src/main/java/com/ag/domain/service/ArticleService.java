package com.ag.domain.service;

import com.ag.domain.constant.StatusType;
import com.ag.domain.exception.ArticleNotFoundException;
import com.ag.domain.model.Article;
import com.ag.domain.repository.ArticleRepository;
import com.ag.domain.service.base.CrudServiceImpl;
import com.ag.domain.util.AuthUtil;
import com.ag.domain.util.TimeUtil;
import com.ag.domain.util.ValidationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class ArticleService extends CrudServiceImpl<Article> {
    private final ArticleRepository articleRepository;
    public static final int MAX_WORD_LENGTH = 5000;
    public static final int MAX_TITLE_LENGTH = 100;

    public Article get(String articleId, int no) {
        return get(new Article(articleId, no));
    }

    public Article delete(String articleId, int no) {
        return delete(new Article(articleId, no));
    }

    @Override
    public Article getImpl(Article article) {
        Article firstArticle = articleRepository.findById(new Article(article.getArticleId(), 0).getId())
                .filter(this::isNormalStatus)
                .orElse(null);

        if (firstArticle == null) {
            return null;
        }

        if (article.getNo() == 0) {
            return firstArticle;
        }

        return articleRepository.findById(article.getId())
                .filter(this::isNormalStatus)
                .orElse(null);
    }

    @Override
    public Article createImpl(Article article) {
        LocalDateTime now = TimeUtil.now();
        int no = 0;
        article = Article.builder()
                .articleId(UUID.randomUUID().toString())
                .no(no)//TODO: get current no
                .authorId(AuthUtil.getUserId())
                .title(article.getTitle())
                .word(article.getWord())
                .likes(0L)
                .status(StatusType.NORMAL)
                .createdTime(now)
                .updatedTime(now).build();

        return articleRepository.save(article);
    }

    @Override
    public Article updateImpl(Article article) {
        Article existing = articleRepository.findById(article.getId())
                .orElseThrow(ArticleNotFoundException::new);
        existing.setTitle(article.getTitle());
        existing.setWord(article.getWord());
        existing.setUpdatedTime(TimeUtil.now());
        return articleRepository.save(existing);
    }

    @Override
    public Article deleteImpl(Article article) {
        Article existing = articleRepository.findById(article.getId())
                .orElseThrow(ArticleNotFoundException::new);
        existing.setStatus(StatusType.DELETED);
        existing.setUpdatedTime(TimeUtil.now());
        return articleRepository.save(existing);
    }

    @Override
    protected void beforeGet(Article article) {
        validateArticleId(article);
        validateNo(article);
    }

    @Override
    protected void beforeCreate(Article article) {
        if (isCreateFirstArticle(article)) {
            validateTitle(article);
        } else {
            validateArticleId(article);
            validateFirstArticleStatusIsNormal(article);
        }
        validateWord(article);
    }

    @Override
    protected void beforeUpdateAndPatch(Article article) {
        validateTitle(article);
        validateWord(article);
        validateStatusIsNormal(article);
        validateHavePermission(article);
    }

    @Override
    protected void beforeDelete(Article article) {
        validateStatusIsNormal(article);
        validateHavePermission(article);
    }

    private boolean isCreateFirstArticle(Article article) {
        return article.getArticleId() == null;
    }

    private boolean isNormalStatus(Article article) {
        return article.getStatus() == StatusType.NORMAL;
    }

    void validateArticleId(Article article) {
        ValidationUtil.assertUUID(article.getArticleId(), "Article id is not a UUID");
    }

    void validateNo(Article article) {
        ValidationUtil.assertInRange(article.getNo(), 0, null, "No must > 0");
    }

    void validateFirstArticleStatusIsNormal(Article article) {
        Article firstArticle = articleRepository.findById(new Article(article.getArticleId(), 0).getId())
                .orElseThrow(ArticleNotFoundException::new);
        ValidationUtil.assertTrue(isNormalStatus(firstArticle), "First article's status is not normal");
    }

    void validateWord(Article article) {//TODO: please update front end
        ValidationUtil.assertInLength(article.getWord(), MAX_WORD_LENGTH,
                "Word length must in {} bytes", MAX_WORD_LENGTH);
    }

    void validateTitle(Article article) {
        if (article.getNo() != null && article.getNo() == 0) {
            ValidationUtil.assertInLength(article.getTitle(), MAX_TITLE_LENGTH,
                    "Title length must in {} bytes", MAX_TITLE_LENGTH);
        } else {
            ValidationUtil.assertTrue(article.getTitle() == null,
                    "Title must null if it is not first article");
        }
    }

    void validateStatusIsNormal(Article article) {
        ValidationUtil.assertTrue(isNormalStatus(article),"Status is not normal");
    }

    void validateHavePermission(Article article) {
        ValidationUtil.assertTrue(AuthUtil.checkPermission(article.getAuthorId()), "No permission to update");
    }

}

