package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.constant.ArtStatusType;
import com.alan10607.leaf.dao.ArticleDAO;
import com.alan10607.leaf.dao.ContentDAO;
import com.alan10607.leaf.dto.PostDTO;
import com.alan10607.leaf.model.Article;
import com.alan10607.leaf.model.Content;
import com.alan10607.leaf.service.ArticleService;
import com.alan10607.leaf.util.RedisKeyUtil;
import com.alan10607.leaf.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ArticleServiceImpl implements ArticleService {
    private ArticleDAO articleDAO;
    private ContentDAO contentDAO;
    private final RedisTemplate redis;
    private final RedisKeyUtil keyUtil;
    private final TimeUtil timeUtil;
    private final static int ART_SET_EXPIRE = 3600;
    private final static int ART_EXPIRE = 3600;

    public List<String> findArtSetFromRedis(long start, long end) {
        if(!redis.hasKey(keyUtil.ART_SET)){
            pullArtSetToRedis();
        }

        Set<ZSetOperations.TypedTuple<String>> zSet = redis.opsForZSet().range(keyUtil.ART_SET, start, end);
        return zSet.stream()
                .map(s -> s.getValue())
                .collect(Collectors.toList());
    }

    private void pullArtSetToRedis() {
        List<String> idList = findArtSet();
        for(int i = 0; i < idList.size(); i++)
            redis.opsForZSet().add(keyUtil.ART_SET,1,timeUtil.BATCH_START + i);

        redis.expire(keyUtil.ART_SET, keyUtil.getRanExp(ART_SET_EXPIRE), TimeUnit.SECONDS);
        log.info("Pull artSet to redis succeed, artSet size={}", idList.size());
    }

    public void updateArtSetFromRedis(String id, LocalDateTime updateTime) {
        redis.opsForZSet().add(keyUtil.ART_SET,id,timeUtil.getRedisScore(updateTime));
        log.info("Update artSet score succeed, id={}", id);
    }

    public void deleteArtSetFromRedis() {
        redis.delete(keyUtil.ART_SET);
        log.info("Delete artSet from redis succeed");
    }

    /**
     * 依照更新回文順率列出文章標題, 若沒有則到DB查詢後放入redis
     * @param idList
     * @return
     */
    public List<PostDTO> findArticleFromRedis(List<String> idList) {
        List<PostDTO> artList = new ArrayList<>();
        for(String id : idList){
            Map<String, Object> artMap = redis.opsForHash().entries(keyUtil.art(id));
            if(artMap.isEmpty()) {
                artList.add(pullArticleToRedis(id));
            }else {
                artList.add(processArticleRedis(id, artMap));
            }

            redis.expire(keyUtil.art(id), keyUtil.getRanExp(ART_EXPIRE), TimeUnit.SECONDS);
        }
        return artList;
    }

    private PostDTO pullArticleToRedis(String id) {
        PostDTO postDTO = new PostDTO();
        try {
            postDTO = findArticle(id);
        }catch (Exception e){
            log.error("Pull Article failed, id={}, put empty data to redis: {}", id, e);
        }

        Map<String, Object> toRedis = Map.of(
                "id", postDTO.getId(),
                "title", postDTO.getTitle(),
                "contNum", postDTO.getContNum(),
                "status", postDTO.getStatus(),
                "updateDate", timeUtil.format(postDTO.getUpdateDate()),
                "createDate", timeUtil.format(postDTO.getCreateDate())
        );
        redis.opsForHash().putAll(keyUtil.art(id), toRedis);
        log.info("Pull art to redis succeed, id={}", id);
        return postDTO;
    }

    private PostDTO processArticleRedis(String id, Map<String, Object> artMap) {
        if(!ArtStatusType.DELETED.equals(artMap.get("status"))){
            return new PostDTO(id,
                    (String) artMap.get("title"),
                    ((Number) artMap.get("contNum")).intValue(),
                    (ArtStatusType) artMap.get("status"),
                    timeUtil.parseStr((String) artMap.get("updateDate")),
                    timeUtil.parseStr((String) artMap.get("createDate"))
            );
        }else{
            return new PostDTO(id,
                    (ArtStatusType) artMap.get("status")
            );
        }
    }

    public void deleteArticleFromRedis(String id) {
        redis.delete(keyUtil.art(id));
        log.info("Delete art from redis succeed");
    }

    public List<String> findArtSet() {
        //獲得最近一個月內前100筆最新更新資料
        return articleDAO.findLatest100Id();
    }

    public PostDTO findArticle(String id) {
        Article article = articleDAO.findById(id).orElseThrow(()
                -> new IllegalStateException("Article not found"));

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
                0,
                ArtStatusType.NEW,
                createAndUpdateTime,
                createAndUpdateTime);

        Content content = new Content(id,
                no,
                author,
                word,
                0L,
                ArtStatusType.NEW,
                createAndUpdateTime,
                createAndUpdateTime);

        createArtAndContTxn(article, content);
    }

    @Transactional
    public void createArtAndContTxn(Article article, Content content) {
        articleDAO.save(article);
        contentDAO.save(content);
    }

    public void createContAndUpdateArt(String id, String author, String word, LocalDateTime createAndUpdateTime) {
        Content content = new Content(id,
                author,
                word,
                0L,
                ArtStatusType.NEW,
                createAndUpdateTime,
                createAndUpdateTime);

        createContAndUpdateArtTxn(id, content);
    }

    @Transactional
    public void createContAndUpdateArtTxn(String id, Content content) {
        int contNum = articleDAO.findContNumByIdWithLock(id).orElseThrow(()
                -> new IllegalStateException("Article not found"));

        content.setNo(contNum);//會剛好是contNum
        contentDAO.save(content);
        articleDAO.incrContNum(id);
    }

    public void updateArticleStatus(String id, ArtStatusType status) {
        Article article = articleDAO.findById(id)
                .orElseThrow(() -> new IllegalStateException("Article not found"));

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
