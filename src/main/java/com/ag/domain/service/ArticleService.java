package com.ag.domain.service;

import com.ag.domain.constant.StatusType;
import com.ag.domain.dto.ArticleDTO;
import com.ag.domain.exception.AgValidationException;
import com.ag.domain.model.Article;
import com.ag.domain.model.Like;
import com.ag.domain.repository.ArticleRepository;
import com.ag.domain.service.base.CrudServiceImpl;
import com.ag.domain.util.PojoFiledUtil;
import com.alan10607.ag.util.AuthUtil;
import com.alan10607.ag.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class ArticleService extends CrudServiceImpl<ArticleDTO> {
    private final ArticleRepository articleRepository;
    private final LikeService likeService;
    public static final int MAX_WORD_LENGTH = 5000;
    public static final int MAX_TITLE_LENGTH = 100;

    public ArticleDTO get(String id, int no) {
        return get(new ArticleDTO(id, no));
    }

    public ArticleDTO delete(String id, int no) {
        return delete(new ArticleDTO(id, no));
    }

    public ArticleDTO patchWord(ArticleDTO articleDTO) {
        ArticleDTO patchDTO = PojoFiledUtil.retainFields(articleDTO, "id", "no", "word");
        return patch(patchDTO);
    }

    public ArticleDTO patchStatus(ArticleDTO articleDTO) {
        ArticleDTO patchDTO = PojoFiledUtil.retainFields(articleDTO, "id", "no", "status");
        return patch(patchDTO);
    }

    @Override
    public ArticleDTO getImpl(ArticleDTO articleDTO) {
        Article article = articleRepository.get(null);
        return returnFilter(article);
    }

    private ArticleDTO returnFilter(Article article) {
        ArticleDTO articleDTO = PojoFiledUtil.convertObject(article, ArticleDTO.class);
        switch (articleDTO.getStatus()) {
            case NORMAL:
                //TODO: prepare after get
                Like like = likeService.get(articleDTO.getId(), articleDTO.getNo(), "userId");
                articleDTO.setLike(like.getState());

//                UserDTO userDTO = userService.get(contentDTO.getAuthorId());
//                contentDTO.setAuthorName(userDTO.getUsername());
//                contentDTO.setAuthorHeadUrl(userDTO.getHeadUrl());
//                contentDTO.setLike(likeService.get(contentDTO.getId(), contentDTO.getNo(), AuthUtil.getUserId()));
                return articleDTO;
            case DELETED:
                return new ArticleDTO(articleDTO.getId(), articleDTO.getNo(), StatusType.DELETED);
            case UNKNOWN:
            default:
                log.info("Article {}/{} not found", articleDTO.getId(), articleDTO.getNo());
                return new ArticleDTO(articleDTO.getId(), articleDTO.getNo(), StatusType.DELETED);
        }
    }

    @Override
    public ArticleDTO createImpl(ArticleDTO articleDTO) {
        LocalDateTime now = TimeUtil.now();
        Article article = Article.builder()
                .id(UUID.randomUUID().toString())
                .no(0)
                .authorId(AuthUtil.getUserId())
                .title(articleDTO.getTitle())
                .word(articleDTO.getWord())
                .likes(0L)
                .status(StatusType.NORMAL)
                .createDate(now)
                .updateDate(now)
                .build();


        article = articleRepository.save(article);
        return returnFilter(article);
    }

    @Override
    public ArticleDTO updateImpl(ArticleDTO articleDTO) {
        LocalDateTime now = TimeUtil.now();
        Article article = Article.builder()
                .id(articleDTO.getId())
                .no(articleDTO.getNo())
                .authorId(articleDTO.getAuthorId())
                .title(articleDTO.getTitle())
                .word(articleDTO.getWord())
                .likes(articleDTO.getLikes())
                .status(articleDTO.getStatus())
                .createDate(articleDTO.getCreateDate())
                .updateDate(now)
                .build();

        article = articleRepository.save(article);
        return returnFilter(article);
    }

    @Override
    public ArticleDTO patchImpl(ArticleDTO articleDTO) {
        return update(articleDTO);
    }

    @Override
    public ArticleDTO deleteImpl(ArticleDTO articleDTO) {
        Article article = PojoFiledUtil.convertObject(articleDTO, Article.class);
        return returnFilter(articleRepository.delete(article));
    }

    @Override
    protected void beforeGet(ArticleDTO articleDTO) {
        validateFirstArticleIsNormal(articleDTO);
    }

    @Override
    protected void beforeCreate(ArticleDTO articleDTO) {
        validateTitle(articleDTO);
        validateWord(articleDTO);
    }

    @Override
    protected void beforeUpdateAndPatch(ArticleDTO articleDTO) {
        validateWord(articleDTO);
        validateStatus(articleDTO);
    }

    @Override
    protected void beforeDelete(ArticleDTO articleDTO) {
        validateStatus(articleDTO);
    }

    void validateWord(ArticleDTO articleDTO) {
        if (StringUtils.isBlank(articleDTO.getWord())) {
            throw new AgValidationException("Word must not be blank", articleDTO);
        }//TODO: please update front end
        if (articleDTO.getWord().getBytes().length > MAX_WORD_LENGTH) {
            throw new AgValidationException("Word length must in {} bytes", articleDTO, MAX_WORD_LENGTH);
        }
    }

    void validateTitle(ArticleDTO articleDTO) {
        if (articleDTO.getNo() == 0 && StringUtils.isBlank(articleDTO.getTitle())) {
            throw new AgValidationException("Title must not be blank when create", articleDTO);
        }
        if (articleDTO.getTitle().getBytes().length > MAX_TITLE_LENGTH) {
            throw new AgValidationException("Title length must in {} bytes", articleDTO, MAX_TITLE_LENGTH);
        }
    }

    void validateStatus(ArticleDTO articleDTO) {
        if (articleDTO.getStatus() != StatusType.NORMAL) {
            throw new AgValidationException("Status is not normal", articleDTO);
        }
    }

    void validateFirstArticleIsNormal(ArticleDTO articleDTO) {
        Article firstArticle = articleRepository.get(articleDTO.getId(), 0);
        if (firstArticle.getStatus() != StatusType.NORMAL) {
            throw new AgValidationException("First article's status is not normal", articleDTO);
        }
    }


}