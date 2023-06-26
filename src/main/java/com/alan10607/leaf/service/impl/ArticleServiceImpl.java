package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.constant.StatusType;
import com.alan10607.leaf.dao.ArticleDAO;
import com.alan10607.leaf.dao.ContentDAO;
import com.alan10607.leaf.dto.PostDTO;
import com.alan10607.leaf.model.Article;
import com.alan10607.leaf.model.Content;
import com.alan10607.leaf.service.ArticleService;
import com.alan10607.leaf.service.TxnService;
import com.alan10607.leaf.util.RedisKeyUtil;
import com.alan10607.leaf.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ArticleServiceImpl implements ArticleService {
    private TxnService txnService;
    private ArticleDAO articleDAO;
    private ContentDAO contentDAO;
    private final RedisTemplate redisTemplate;
    private final DefaultRedisScript createIdSetScript;
    private final RedisKeyUtil keyUtil;
    private final TimeUtil timeUtil;
    private static final int ART_EXPIRE = 3600;
    private static final int POST_SIZE = 100;

    /**
     * 依照更新流言順率列出文章id, string格式, 在jvm做split減少redis每次查詢zset的負擔
     * @return
     */
    public String findIdStrFromRedis() {
        if(!redisTemplate.hasKey(keyUtil.ID_STR)){
            setIdStrToRedis();
        }

        return (String) redisTemplate.opsForValue().get(keyUtil.ID_STR);
    }

    private void setIdStrToRedis() {
        List<String> artList = findIdSetFromRedis(0, POST_SIZE - 1);
        String str = artList.stream().collect(Collectors.joining(","));
        redisTemplate.opsForValue().set(keyUtil.ID_STR, str);
        log.info("Set idStr to redis succeed, join size={}", artList.size());
    }

    public void deleteIdStrFromRedis() {
        redisTemplate.delete(keyUtil.ID_STR);
        log.info("Delete idStr from redis succeed");
    }

    /**
     * 查詢文章的id, 依照最新留言時間先後列出, 若沒有則從DB獲取
     * redis: O(log(N)+M), N為zset size, M為回傳大小
     * @param start
     * @param end
     * @return
     */
    public List<String> findIdSetFromRedis(long start, long end) {
        if(!redisTemplate.hasKey(keyUtil.ID_SET)){
            pullIdSetToRedis();
        }

        Set<String> zSet = redisTemplate.opsForZSet().range(keyUtil.ID_SET, start, end);
        return zSet.stream().collect(Collectors.toList());
    }

    private void pullIdSetToRedis() {
        List<String> idList = findLatestId();
        for(int i = 0; i < idList.size(); i++)
            redisTemplate.opsForZSet().add(keyUtil.ID_SET, idList.get(i),timeUtil.BATCH_START + i);

        log.info("Pull idSet to redis succeed, idSet size={}", idList.size());
    }

    /**
     * 新增新的id到set, 如果超出文章數量限制會zpopmax減少大小
     * @param id
     * @param updateTime
     */
    public void createIdSetFromRedis(String id, LocalDateTime updateTime) {
        if(!redisTemplate.hasKey(keyUtil.ID_SET)){
            pullIdSetToRedis();
        }
        Long res = (Long) redisTemplate.execute(createIdSetScript,
                Arrays.asList(keyUtil.ID_SET),
                id,
                Long.toString(timeUtil.getRedisScore(updateTime)),
                Integer.toString(keyUtil.MAX_ID_SIZE));
        log.info("Create idSet from redis succeed, id={}, popSet={}", id, res == 1);
    }

    /**
     * 更新使最新留言置頂, zSet的score採用時間倒敘, 新的時間會是較小, 使zadd從O(log(N))變為O(1)
     * @param id
     * @param updateTime
     */
    public void updateIdSetFromRedis(String id, LocalDateTime updateTime) {
        if(!redisTemplate.hasKey(keyUtil.ID_SET)){
            pullIdSetToRedis();
        }
        redisTemplate.opsForZSet().add(keyUtil.ID_SET, id, timeUtil.getRedisScore(updateTime));
        log.info("Update idSet score succeed, id={}", id);
    }

    public void deleteIdSetValueFromRedis(String id) {
        redisTemplate.opsForZSet().remove(keyUtil.ID_SET, id);
        log.info("Delete idSet id={} from redis succeed", id);
    }

    public void deleteIdSetFromRedis() {
        redisTemplate.delete(keyUtil.ID_SET);
        log.info("Delete idSet from redis succeed");
    }

    /**
     * 查詢art文章總表, 若沒有則到DB查詢, 並設置過期時間
     * @param id
     * @return
     */
    public PostDTO findArticleFromRedis(String id) {
        Map<String, Object> artMap = redisTemplate.opsForHash().entries(keyUtil.art(id));
        PostDTO art = new PostDTO();
        if(artMap.isEmpty()) {
            art = pullArticleToRedis(id);
        }else {
            art = processArticleRedis(id, artMap);
        }
        redisTemplate.expire(keyUtil.art(id), keyUtil.getRanExp(ART_EXPIRE), TimeUnit.SECONDS);

        if(art.getStatus() == StatusType.UNKNOWN)
            throw new IllegalStateException(String.format("Article not found, id: %s", id));

        return art;
    }

    public List<PostDTO> findArticleFromRedis(List<String> idList) {
        List<PostDTO> artList = new ArrayList<>();
        for(String id : idList)
            artList.add(findArticleFromRedis(id));

        return artList;
    }

    private PostDTO pullArticleToRedis(String id) {
        PostDTO postDTO = new PostDTO();
        try {
            postDTO = findArticle(id);
        }catch (Exception e){
            postDTO.setStatus(StatusType.UNKNOWN);
            redisTemplate.opsForHash().putAll(keyUtil.art(id), Map.of("status", postDTO.getStatus()));
            log.error("Pull Article failed, id={}, put empty data to redis", id);
            return postDTO;
        }

        Map<String, Object> toRedis = Map.of(
                "id", postDTO.getId(),
                "title", postDTO.getTitle(),
                "contNum", postDTO.getContNum(),
                "status", postDTO.getStatus(),
                "updateDate", timeUtil.format(postDTO.getUpdateDate()),
                "createDate", timeUtil.format(postDTO.getCreateDate())
        );
        redisTemplate.opsForHash().putAll(keyUtil.art(id), toRedis);
        log.info("Pull art to redis succeed, id={}", id);
        return postDTO;
    }

    private PostDTO processArticleRedis(String id, Map<String, Object> artMap) {
        StatusType status = (StatusType) artMap.get("status");
        switch(status){
            case UNKNOWN :
            case DELETED :
                return new PostDTO(id,
                        (StatusType) artMap.get("status")
                );
            default :
                return new PostDTO(id,
                        (String) artMap.get("title"),
                        ((Number) artMap.get("contNum")).intValue(),
                        (StatusType) artMap.get("status"),
                        timeUtil.parseStr((String) artMap.get("updateDate")),
                        timeUtil.parseStr((String) artMap.get("createDate"))
                );
        }
    }

    /**
     * 查詢該文章的留言數量(含主文), 若不在redis會再查詢一次
     * @param id
     * @return
     */
    public int findArtContNumFromRedis(String id) {
        if(!redisTemplate.hasKey((keyUtil.art(id)))){
            PostDTO art = findArticleFromRedis(id);
            return art.getContNum();
        }

        return ((Number) redisTemplate.opsForHash().get(keyUtil.art(id), "contNum")).intValue();
    }

    public void deleteArticleFromRedis(String id) {
        redisTemplate.delete(keyUtil.art(id));
        log.info("Delete art from redis succeed, id={}", id);
    }

    /**
     * 獲得最近一個月內最新更新資料
     * @return
     */
    public List<String> findLatestId() {
        return articleDAO.findLatest100Id(StatusType.NEW.name());
    }

    public PostDTO findArticle(String id) {
        Article article = articleDAO.findById(id)
                .orElseThrow(() -> new IllegalStateException("Article not found"));

        return new PostDTO(article.getId(),
                article.getTitle(),
                article.getContNum(),
                article.getStatus(),
                article.getUpdateDate(),
                article.getCreateDate());
    }

    public void createArtAndCont(String id, int no, String title, String author, String word, LocalDateTime createAndUpdateTime) {
        articleDAO.findById(id).ifPresent((a) -> {
            throw new IllegalStateException("Article id already exist");
        });

        contentDAO.findByIdAndNo(id, no).ifPresent((c) -> {
            throw new IllegalStateException("Content id already exist");
        });

        Article article = new Article(id,
                title,
                1,
                StatusType.NEW,
                createAndUpdateTime,
                createAndUpdateTime);

        Content content = new Content(id,
                no,
                author,
                word,
                0L,
                StatusType.NEW,
                createAndUpdateTime,
                createAndUpdateTime);

        txnService.createArtAndContTxn(article, content);
    }

    public int createContAndUpdateArt(String id, String author, String word, LocalDateTime createAndUpdateTime) {
        Content content = new Content(id,
                author,
                word,
                0L,
                StatusType.NEW,
                createAndUpdateTime,
                createAndUpdateTime);

        return txnService.createContAndUpdateArtTxn(id, content);
    }

    public void updateArticleStatus(String id, String userId, StatusType status) {
        Article article = articleDAO.findById(id)
                .orElseThrow(() -> new IllegalStateException("Article not found"));

        Content content = contentDAO.findByIdAndNo(id, 0)
                .orElseThrow(() -> new IllegalStateException("Content no 0 not found"));

        if(!userId.equals(content.getAuthor()))
            throw new IllegalStateException("No authority to modify");

        article.setStatus(status);
        article.setUpdateDate(timeUtil.now());
        articleDAO.save(article);
    }

    public void deleteArticle(String id) {
        Article article = articleDAO.findById(id)
                .orElseThrow(() -> new IllegalStateException("Article not found"));

        articleDAO.delete(article);
    }

}
