package com.ag.domain.service;

import com.ag.domain.constant.StatusType;
import com.ag.domain.exception.ArticleNotFoundException;
import com.ag.domain.model.Article;
import com.ag.domain.repository.ArticleRepository;
import com.ag.domain.service.base.CrudServiceImpl;
import com.ag.domain.util.AuthUtil;
import com.ag.domain.util.PojoFiledUtil;
import com.ag.domain.util.TimeUtil;
import com.ag.domain.util.ValidationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class ArticleService extends CrudServiceImpl<Article> {
    private final ArticleRepository articleRepository;
    private final LikeService likeService;
    public static final int MAX_WORD_LENGTH = 5000;
    public static final int MAX_TITLE_LENGTH = 100;

    public Article get(String articleId, int no) {
        return get(new Article(articleId, no));
    }

    public Article delete(String articleId, int no) {
        return delete(new Article(articleId, no));
    }

    public Article patchTitle(Article article) {
        Article patchedArticle = PojoFiledUtil.retainFields(article, "articleId", "no", "title");
        return patch(patchedArticle);
    }

    public Article patchWord(Article article) {
        Article patchedArticle = PojoFiledUtil.retainFields(article, "articleId", "no", "word");
        return patch(patchedArticle);
    }

    public Article patchStatus(Article article) {
        Article patchedArticle = PojoFiledUtil.retainFields(article, "articleId", "no", "status");
        return patch(patchedArticle);
    }

    @Override
    public Article getImpl(Article article) {
        return articleRepository.findById(article.getId()).orElse(null);
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
                .updatedTime(now)
                .build();

        return articleRepository.save(article);
    }

    @Override
    public Article updateImpl(Article article) {
        Article existing = articleRepository.findById(article.getId()).orElseThrow(ArticleNotFoundException::new);
        existing.setTitle(article.getTitle());
        existing.setWord(article.getWord());
        existing.setStatus(article.getStatus());
        existing.setUpdatedTime(TimeUtil.now());
        return articleRepository.save(existing);
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
        validateArticleId(article);
        validateNo(article);
        validateFirstArticleStatusIsNormal(article);
    }

    @Override
    protected void beforeCreate(Article article) {
        if(article.getArticleId() == null){
            validateTitle(article);
        }else{
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

    void validateArticleId(Article article){
        ValidationUtil.checkArgument(StringUtils.isNotBlank(article.getArticleId()),
                "Article id must not be blank", article);
    }

    void validateNo(Article article){
        ValidationUtil.checkArgument(article.getNo() != null && article.getNo() >= 0,
                "No must > 0", article);
    }

    void validateFirstArticleStatusIsNormal(Article article) {
        Article firstArticle = articleRepository.findByArticleIdAndNo(article.getArticleId(), 0)
                .orElseThrow(ArticleNotFoundException::new);
        ValidationUtil.checkArgument(firstArticle.getStatus() == StatusType.NORMAL,
                "First article's status is not normal", article);
    }

    void validateWord(Article article) {
        ValidationUtil.checkArgument(StringUtils.isNotBlank(article.getWord()),
                "Word must not be blank", article);
        ValidationUtil.checkArgument(article.getWord().getBytes().length <= MAX_WORD_LENGTH,
                "Word length must in {} bytes", article, MAX_WORD_LENGTH);//TODO: please update front end
    }

    void validateTitle(Article article) {
        if(article.getNo() == null || article.getNo() == 0){
            ValidationUtil.checkArgument(StringUtils.isNotBlank(article.getTitle()),
                    "Title must not be blank", article);
            ValidationUtil.checkArgument(article.getTitle().getBytes().length <= MAX_TITLE_LENGTH,
                    "Title length must in {} bytes", article, MAX_TITLE_LENGTH);
        }else{
            ValidationUtil.checkArgument(article.getTitle() == null,
                    "Title must null if it is not first article", article);
        }
    }

    void validateStatusIsNormal(Article article) {
        ValidationUtil.checkArgument(article.getStatus() == StatusType.NORMAL,
                "Status is not normal", article);
    }

    void validateHavePermission(Article article) {
        ValidationUtil.checkArgument(AuthUtil.getUserId().equals(article.getAuthorId()),
                "No permission to update", article);
    }

}

