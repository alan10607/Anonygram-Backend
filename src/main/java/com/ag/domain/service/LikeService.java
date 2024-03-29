package com.ag.domain.service;

import com.ag.domain.model.Article;
import com.ag.domain.model.Like;
import com.ag.domain.repository.LikeRepository;
import com.ag.domain.service.base.CrudServiceImpl;
import com.ag.domain.util.AuthUtil;
import com.ag.domain.util.ValidationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class LikeService extends CrudServiceImpl<Like> {
    private final ArticleService articleService;
    private final LikeRepository likeRepository;

    public Like get(String articleId, int no) {
        return this.get(new Like(articleId, no, AuthUtil.getUserId()));
    }

    public Like create(String articleId, int no) {
        return this.create(new Like(articleId, no, AuthUtil.getUserId()));
    }

    public Like delete(String articleId, int no) {
        return this.delete(new Like(articleId, no, AuthUtil.getUserId()));
    }

    @Override
    public Like getImpl(Like like) {
        return likeRepository.findById(like.getId()).orElse(null);
    }

    @Override
    public Like createImpl(Like like) {
        return likeRepository.save(like);
    }

    @Override
    public Like updateImpl(Like like) {
        // Ignored update
        return like;
    }

    @Override
    public Like deleteImpl(Like like) {
        likeRepository.deleteById(like.getId());
        return like;
    }

    @Override
    protected void beforeGet(Like like) {
        validateArticleId(like);
        validateNo(like);
    }

    @Override
    protected void beforeCreate(Like like) {
        validateArticleId(like);
        validateNo(like);
        validateHavePermission(like);
        validateArticleIsExist(like);
    }

    @Override
    protected void beforeDelete(Like like) {
        validateHavePermission(like);
        validateArticleIsExist(like);
    }

    void validateArticleId(Like like) {
        ValidationUtil.assertUUID(like.getArticleId(), "Article id is not a UUID");
    }

    void validateNo(Like like) {
        ValidationUtil.assertInRange(like.getNo(), 0, null, "No must >= 0");
    }

    void validateHavePermission(Like like) {
        ValidationUtil.assertTrue(AuthUtil.isUserEquals(like.getUserId()), "No permission to update");
    }

    void validateArticleIsExist(Like like) {
        Article article = articleService.get(like.getArticleId(), like.getNo());
        ValidationUtil.assertTrue(article != null, "Article not found for article id and no");
    }

}
