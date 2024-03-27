package com.ag.domain.service;

import com.ag.domain.constant.ArticleStatus;
import com.ag.domain.dto.ArticleDTO;
import com.ag.domain.dto.QueryDTO;
import com.ag.domain.dto.UserDTO;
import com.ag.domain.model.Article;
import com.ag.domain.model.ForumUser;
import com.ag.domain.model.Like;
import com.ag.domain.repository.ArticleRepository;
import com.ag.domain.repository.LikeRepository;
import com.ag.domain.repository.UserRepository;
import com.ag.domain.repository.esQuery.ArticleQueryHandler;
import com.ag.domain.repository.esQuery.LikeQueryHandler;
import com.ag.domain.repository.esQuery.UserQueryHandler;
import com.ag.domain.util.AuthUtil;
import com.ag.domain.util.PojoFiledUtil;
import com.ag.domain.util.ValidationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class QueryService {
    private final ArticleQueryHandler articleQueryHandler;
    private final UserQueryHandler userQueryHandler;
    private final LikeQueryHandler likeQueryHandler;
    private final ArticleRepository articleRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    private static final int MAX_QUERY_ARTICLE_SIZE = 10;
    private static final int PAGEABLE_SIZE = 10;

    public List<String> queryArticleIds() {
        return articleQueryHandler.searchLatestArticleId();
    }

    public List<QueryDTO> queryMultiArticle(List<String> articleIdList, int page) {
        validateArticleIdList(articleIdList);

        return articleIdList.stream()
                .map(articleId -> queryArticle(articleId, page))
                .collect(Collectors.toList());
    }

    public QueryDTO queryArticle(String articleId, int page) {
        validateArticleId(articleId);
        validatePage(page);

        ArticleStatus status = getFirstArticleStatus(articleId);
        switch (status) {
            case NORMAL:
                int noStart = page == 0 ? 0 : (page - 1) * PAGEABLE_SIZE;
                int noEnd = page == 0 ? 0 : page * PAGEABLE_SIZE - 1;
                List<ArticleDTO> articles = articleQueryHandler.searchByArticleIdAndNo(articleId, noStart, noEnd)
                        .stream()
                        .map(this::prepareArticle)
                        .collect(Collectors.toList());

                QueryDTO queryDTO = new QueryDTO(articleId, ArticleStatus.NORMAL);
                queryDTO.setArticleList(articles);
                queryDTO.setCount(articleRepository.countByArticleId(articleId));
                return queryDTO;
            default:
                return new QueryDTO(articleId, status);
        }
    }

    private ArticleDTO prepareArticle(Article article) {
        switch (article.getStatus()) {
            case NORMAL:
                ArticleDTO result = PojoFiledUtil.convertObject(article, ArticleDTO.class);

                ForumUser author = userRepository.findById(article.getAuthorId())
                        .orElse(new ForumUser.AnonymousUserBuilder(article.getArticleId()).build());
                result.setAuthorName(author.getUsername());
                result.setAuthorHeadUrl(author.getHeadUrl());

                boolean like = likeRepository.findById(new Like(article.getArticleId(), article.getNo(), AuthUtil.getUserId()).getId()).isPresent();
                result.setLike(like);

                long likeCount = likeQueryHandler.countByArticleIdAndNo(article.getArticleId(), article.getNo());
                result.setLikeCount(likeCount);

                return result;
            default:
                return new ArticleDTO(article.getArticleId(), article.getNo(), article.getStatus());
        }
    }

    private ArticleStatus getFirstArticleStatus(String articleId) {
        return articleRepository.findById(new Article(articleId, 0).getId())
                .map(Article::getStatus)
                .orElse(ArticleStatus.UNKNOWN);
    }

    public List<ArticleDTO> queryArticle(String keyword) {
        return articleQueryHandler.searchByWordOrTitle(keyword)
                .stream()
                .map(this::prepareArticle)
                .collect(Collectors.toList());
    }


    public List<UserDTO> queryUser(String keyword) {
        return userQueryHandler.searchByUsername(keyword)
                .stream()
                .map(user -> PojoFiledUtil.convertObject(user, UserDTO.class))
                .map(userDTO -> PojoFiledUtil.retainFields(userDTO, "id", "username"))
                .collect(Collectors.toList());
    }

    void validateArticleIdList(List<String> articleIdList) {
        ValidationUtil.assertTrue(articleIdList.size() <= MAX_QUERY_ARTICLE_SIZE,
                "Article id list length must < {}", MAX_QUERY_ARTICLE_SIZE);
    }

    void validateArticleId(String articleId) {
        ValidationUtil.assertUUID(articleId, "Article id is not a UUID");
    }

    void validatePage(int page) {
        ValidationUtil.assertInRange(page, 0, null, "Page must >= 0");
    }
}

