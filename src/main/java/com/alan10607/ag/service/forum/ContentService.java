package com.alan10607.ag.service.forum;

import com.alan10607.ag.constant.StatusType;
import com.alan10607.ag.dao.ArticleDAO;
import com.alan10607.ag.dao.ContentDAO;
import com.alan10607.ag.dto.ContentDTO;
import com.alan10607.ag.dto.LikeDTO;
import com.alan10607.ag.dto.UserDTO;
import com.alan10607.ag.exception.AnonygramIllegalStateException;
import com.alan10607.ag.model.Content;
import com.alan10607.ag.service.auth.UserService;
import com.alan10607.ag.service.redis.ArticleRedisService;
import com.alan10607.ag.service.redis.ContentRedisService;
import com.alan10607.ag.service.redis.LockRedisService;
import com.alan10607.ag.util.AuthUtil;
import com.alan10607.ag.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ContentService {
    private final UserService userService;
    private final IdService idService;
    private final LikeService likeService;
    private final ArticleRedisService articleRedisService;
    private final ContentRedisService contentRedisService;
    private final LockRedisService lockRedisService;
    private final ArticleDAO articleDAO;
    private final ContentDAO contentDAO;

    public List<ContentDTO> get(String id, List<Integer> noList) {
        return noList.stream().map(no -> get(id, no)).collect(Collectors.toList());
    }

    public ContentDTO get(String id, int no) {
        ContentDTO contentDTO = contentRedisService.get(id, no);
        if(StringUtils.isBlank(contentDTO.getId()) || contentDTO.getNo() == null){
            lockRedisService.lockByContent(id, no, () -> pullToRedis(id, no));
            contentDTO = contentRedisService.get(id, no);
        }
        contentRedisService.expire(id, no);

        return contentFilter(contentDTO);
    }

    private void pullToRedis(String id, int no) {
        ContentDTO contentDTO = contentDAO.findByIdAndNo(id, no)
            .map(content -> {
                UserDTO userDTO = userService.get(content.getAuthorId());
                return new ContentDTO(content.getId(),
                        content.getNo(),
                        content.getAuthorId(),
                        userDTO.getUsername(),
                        userDTO.getHeadUrl(),
                        content.getWord(),
                        content.getLikes(),
                        content.getStatus(),
                        content.getCreateDate(),
                        content.getUpdateDate());
            }).orElseGet(() -> {
                log.error("Pull Content failed, id={}, no={}, put empty data to redis", id, no);
                return new ContentDTO(id, no, StatusType.UNKNOWN);
            });

        contentRedisService.set(contentDTO);
        contentRedisService.expire(id, no);
        log.info("Pull Content to redis succeed, id={}, no={}", id, no);
    }

    private ContentDTO contentFilter(ContentDTO contentDTO) {
        switch(contentDTO.getStatus()){
            case NORMAL:
                contentDTO.setLike(likeService.get(contentDTO.getId(), contentDTO.getNo(), AuthUtil.getUserId()));
                return contentDTO;
            case DELETED :
                return new ContentDTO(contentDTO.getId(), contentDTO.getNo(), StatusType.DELETED);
            case UNKNOWN :
            default:
                log.info("Content not found, id={}, no={}", contentDTO.getId(), contentDTO.getNo());
                return new ContentDTO(contentDTO.getId(), contentDTO.getNo(), StatusType.UNKNOWN);
        }
    }

    public Integer getContentSize(String id){
        return contentDAO.countById(id);
    }

    /**
     * MySQL InnoDB engine does not support auto-increment with multiple primary keys.
     * Instead, use countByIdWithLock with "LOCK IN SHARE MODE" as an alternative to the auto-increment rule.
     * countByIdWithLock will only lock the rows with the same id and will not lock other rows with different id.
     * @param contentDTO
     * @return
     */
    @Transactional
    public int create(ContentDTO contentDTO) {
        prepareCreateValue(contentDTO);

        Content content = new Content(contentDTO.getId(),
                contentDTO.getNo(),
                contentDTO.getAuthorId(),
                HtmlUtils.htmlEscape(contentDTO.getWord()),
                0L,
                StatusType.NORMAL,
                contentDTO.getCreateDate(),
                contentDTO.getCreateDate());

        contentDAO.save(content);
        contentDTO.setNo(content.getNo());
        contentRedisService.delete(contentDTO.getId(), contentDTO.getNo());
        articleRedisService.delete(contentDTO.getId());
        idService.set(contentDTO.getId());
        return content.getNo();
    }

    private void prepareCreateValue(ContentDTO contentDTO){
        articleDAO.findById(contentDTO.getId()).ifPresentOrElse(
            article -> {
                if(article.getStatus() != StatusType.NORMAL) {
                    throw new AnonygramIllegalStateException("Article status of this content not normal, id={}", contentDTO.getId());
                }
            },
            () -> {
                throw new AnonygramIllegalStateException("Article not found, id={}", contentDTO.getId());
            });

        List<Object[]> query = contentDAO.countByIdWithLock(contentDTO.getId());
        contentDTO.setNo(((BigInteger) query.get(0)[0]).intValue());

        contentDTO.setAuthorId(AuthUtil.getUserId());
        if(contentDTO.getCreateDate() == null) {
            contentDTO.setCreateDate(TimeUtil.now());
        }
    }

    @Transactional
    public void updateStatus(String id, int no, StatusType status) {
        Content content = contentDAO.findByIdAndNo(id, no).orElseThrow(() ->
                new AnonygramIllegalStateException("Content not found, id={}, no={}", id, no));

        if(!AuthUtil.getUserId().equals(content.getAuthorId()))
            throw new AnonygramIllegalStateException("No authority to modify");

        content.setStatus(status);
        content.setUpdateDate(TimeUtil.now());
        contentDAO.save(content);
        contentRedisService.delete(id, no);
    }

    public void updateLike(String id, int no, boolean like) {
        ContentDTO contentDTO = get(id, no);
        if(contentDTO.getStatus() != StatusType.NORMAL){
            throw new AnonygramIllegalStateException("Content status is {}, id={}, no={}",
                    contentDTO.getStatus(), contentDTO.getId(), contentDTO.getNo());
        }
        LikeDTO likeDTO = new LikeDTO(id, no, AuthUtil.getUserId(), like);
        likeService.set(likeDTO);
        contentRedisService.updateLikes(id, no, likeDTO.getLike() ? 1 : -1);
    }
}