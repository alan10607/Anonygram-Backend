package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.constant.ArtStatusType;
import com.alan10607.leaf.dao.ContentDAO;
import com.alan10607.leaf.dto.PostDTO;
import com.alan10607.leaf.model.Content;
import com.alan10607.leaf.service.ContLikeService;
import com.alan10607.leaf.service.ContentService;
import com.alan10607.leaf.util.RedisKeyUtil;
import com.alan10607.leaf.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class ContentServiceImpl implements ContentService {
    private ContLikeService contLikeService;
    private ContentDAO contentDAO;
    private final RedisTemplate redisTemplate;
    private final RedisKeyUtil keyUtil;
    private final TimeUtil timeUtil;
    private final static int CONT_EXPIRE = 3600;


    public List<PostDTO> findContentFromRedis(String id, int start, int end, String userId) {
        List<PostDTO> contList = new ArrayList<>();
        for(int no = start; no <= end; no++){
            Map<String, Object> contMap = redisTemplate.opsForHash().entries(keyUtil.cont(id, no));
            PostDTO cont = new PostDTO();
            if(contMap.isEmpty()) {
                cont = pullContentToRedis(id, no, userId);
            }else{
                cont = processContentRedis(id, no, userId, contMap);
            }
            redisTemplate.expire(keyUtil.cont(id, no), keyUtil.getRanExp(CONT_EXPIRE), TimeUnit.SECONDS);

            if(cont.getStatus() == ArtStatusType.UNKNOWN)
                throw new IllegalStateException(String.format("Content not found, id: %s, no: %s", id, no));

            contList.add(cont);
        }

        return contList;
    }

    private PostDTO pullContentToRedis(String id, int no, String userId) {
        PostDTO postDTO = new PostDTO();
        try {
            postDTO = findContent(id, no);
        }catch (Exception e){
            postDTO.setStatus(ArtStatusType.UNKNOWN);
            redisTemplate.opsForHash().putAll(keyUtil.cont(id, no), Map.of("status", postDTO.getStatus()));
            log.error("Pull Content failed, id={}, no={}, put empty data to redis", id, no);
            return postDTO;
        }

        Map<String, Object> toRedis = Map.of(
                "author", postDTO.getAuthor(),
                "word", postDTO.getWord(),
                "likes", postDTO.getLikes(),
                "status", postDTO.getStatus(),
                "updateDate", timeUtil.format(postDTO.getUpdateDate()),
                "createDate", timeUtil.format(postDTO.getCreateDate())
        );
        redisTemplate.opsForHash().putAll(keyUtil.cont(id, no), toRedis);
        log.info("Pull cont to redis succeed, id={}, no={}", id, no);

        postDTO.setIsUserLike(contLikeService.findContLikeFromRedis(id, no, userId));
        return postDTO;
    }

    private PostDTO processContentRedis(String id, int no, String userId, Map<String, Object> contMap) {
        ArtStatusType status = (ArtStatusType) contMap.get("status");
        switch(status){
            case UNKNOWN :
            case DELETED :
                return new PostDTO(id,
                        no,
                        (ArtStatusType) contMap.get("status")
                );
            default :
                return new PostDTO(id,
                        no,
                        (String) contMap.get("author"),
                        (String) contMap.get("word"),
                        ((Number) contMap.get("likes")).longValue(),
                        (ArtStatusType) contMap.get("status"),
                        timeUtil.parseStr((String) contMap.get("updateDate")),
                        timeUtil.parseStr((String) contMap.get("createDate")),
                        contLikeService.findContLikeFromRedis(id, no, userId)
                );
        }
    }

    public void deleteContentFromRedis(String id, int no) {
        redisTemplate.delete(keyUtil.cont(id, no));
        log.info("Delete cont from redis succeed");
    }

    public void updateContentLikesFromRedis(String id, int no, long incr) {
        redisTemplate.opsForHash().increment(keyUtil.cont(id, no), "likes", incr);
    }

    public PostDTO findContent(String id, int no) {
        Content content = contentDAO.findByIdAndNo(id, no)
                .orElseThrow(() -> new IllegalStateException("Content not found"));

        return new PostDTO(content.getId(),
                content.getNo(),
                content.getAuthor(),
                content.getWord(),
                content.getLikes(),
                content.getStatus(),
                content.getUpdateDate(),
                content.getCreateDate());
    }

    public void updateContentStatus(String id, int no, ArtStatusType status) {
        Content content = contentDAO.findByIdAndNo(id, no)
                .orElseThrow(() -> new IllegalStateException("Content not found"));

        content.setStatus(status);
        content.setUpdateDate(timeUtil.now());
        contentDAO.save(content);
    }

    public void deleteContent(String id, int no) {
        Content content = contentDAO.findByIdAndNo(id, no)
                .orElseThrow(() -> new IllegalStateException("Content not found"));

        contentDAO.delete(content);
    }


}