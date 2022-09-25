package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.constant.ArtStatusType;
import com.alan10607.leaf.constant.LeafRoleType;
import com.alan10607.leaf.dao.ArticleDAO;
import com.alan10607.leaf.dao.ContentDAO;
import com.alan10607.leaf.dto.ArticleDTO;
import com.alan10607.leaf.dto.ContentDTO;
import com.alan10607.leaf.model.Article;
import com.alan10607.leaf.model.Content;
import com.alan10607.leaf.model.LeafRole;
import com.alan10607.leaf.model.LeafUser;
import com.alan10607.leaf.service.UserService;
import com.alan10607.leaf.util.RedisKeyUtil;
import com.alan10607.leaf.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PostServiceImpl {
    private ArticleDAO articleDAO;
    private ContentDAO contentDAO;
    private final RedisTemplate redisTemplate;
    private final RedissonClient redisson;
    private final RedisKeyUtil redisKeyUtil;
    private final TimeUtil timeUtil;

    @Bean
    CommandLineRunner run2(ArticleDAO articleDAO, ContentDAO contentDAO) {
        return args -> {
            String id1 = UUID.randomUUID().toString();
            String id2 = UUID.randomUUID().toString();
            articleDAO.save(new Article(id1,
                    "title1",
                    "author1",
                    0L,
                    "hahahahaha",
                    ArtStatusType.NORMAL.name(),
                    timeUtil.now(),
                    getRevTimeScore()));

            articleDAO.save(new Article(id2,
                    "title2",
                    "author2",
                    10L,
                    "hahahahaha2",
                    ArtStatusType.NORMAL.name(),
                    timeUtil.now(),
                    getRevTimeScore()));

            contentDAO.save(new Content(UUID.randomUUID().toString(),
                    "contAu1",
                    100L,
                    "contWord",
                    ArtStatusType.NORMAL.name(),
                    timeUtil.now(),
                    0L,
                    "parentId"));
        };
    }
    public void initArtSetRedis(){
        List<Article> articleList = articleDAO.findTop10ByOrderByCreateDateDesc();

        List<Map<String, Object>> mapList = articleList.stream().map((article) -> {
                Map<String, Object> map = Map.of(
                        "id", article.getId(),
                        "title", article.getTitle(),
                        "author", article.getAuthor(),
                        "like", article.getLikes(),
                        "word", article.getWord(),
                        "status", article.getStatus(),
                        "createDate", article.getCreateDate());
                return map;

            }
        ).collect(Collectors.toList());

//        for(Map<String, Object> map : mapList){
//            String id = (map.get("id");
//            redisTemplate.opsForHash().putAll("data:art:" + id, map);
//            redisTemplate.opsForZSet().add("data:artSet", id, getRevTimeScore());
//
//        }

    }

    public List<ArticleDTO> findArticle(long start, long end) {
        //會自動sort嗎?
        Set<ZSetOperations.TypedTuple<String>> zSet = redisTemplate.opsForZSet().rangeWithScores(redisKeyUtil.ART_SET, start, end);
        List<String> hashList = zSet.stream()
                .map(s -> s.getValue())
                .collect(Collectors.toList());

        List<ArticleDTO> artList = new ArrayList<>();
        for(String id : hashList){
            Map<String, Object> artMap = redisTemplate.opsForHash().entries(redisKeyUtil.art(id));
            if(ArtStatusType.NORMAL.equals(artMap.get("status"))){
                artList.add(new ArticleDTO(id,
                        (String) artMap.get("title"),
                        (String) artMap.get("author"),
                        ((Number) artMap.get("like")).longValue(),
                        (String) artMap.get("word"),
                        timeUtil.parseString((String) artMap.get("createDate"))
                ));
            }
        }

        return artList;
    }


    public void createArticle(ArticleDTO articleDTO) {
        String id = UUID.randomUUID().toString();
        Map<String, Object> artMap = Map.of(
                "id", id,
                "title", articleDTO.getTitle(),
                "author", articleDTO.getAuthor(),
                "like", 0L,
                "word", articleDTO.getWord(),
                "createDate", timeUtil.nowString(),
                "status", ArtStatusType.NORMAL
        );

        redisTemplate.opsForHash().putAll(redisKeyUtil.art(id), artMap);
        redisTemplate.opsForZSet().add(redisKeyUtil.ART_SET, id, getRevTimeScore());
    }

    public void deleteArticle(ArticleDTO articleDTO) throws Exception {
        redisTemplate.opsForHash().put(redisKeyUtil.art(articleDTO.getId()), "status", ArtStatusType.DELETED);
    }


    public List<ContentDTO> findContent(String id, long start, long end) {
        Set<ZSetOperations.TypedTuple<String>> zSet = redisTemplate.opsForZSet().rangeWithScores(redisKeyUtil.contSet(id), start, end);
        List<String> hashList = zSet.stream()
                .map(s -> s.getValue())
                .collect(Collectors.toList());

        List<ContentDTO> contList = new ArrayList<>();
        for(String contentId : hashList){
            Map<String, Object> contMap = redisTemplate.opsForHash().entries(redisKeyUtil.cont(contentId));

            if(contMap.isEmpty())
                continue;;

            if(ArtStatusType.NORMAL.equals(contMap.get("status"))){
                contList.add(new ContentDTO(contentId,
                        (String) contMap.get("author"),
                        ((Number) contMap.get("like")).longValue(),
                        (String) contMap.get("word"),
                        ((ArtStatusType) contMap.get("status")).name(),
                        timeUtil.parseString((String) contMap.get("createDate"))
                ));
            }else{
                contList.add(new ContentDTO(contentId,
                        "",
                        0L,
                        "Nothing here~~",
                        ((ArtStatusType) contMap.get("status")).name(),
                        LocalDateTime.of(0,0,0,0,0,0,0)
                ));
            }
        }

        return contList;
    }


    public void createContent(String parentId, ContentDTO contentDTO) {
        String id = UUID.randomUUID().toString();
        Map<String, Object> contMap = Map.of(
                "id", id,
                "author", contentDTO.getAuthor(),
                "like", 0L,
                "word", contentDTO.getWord(),
                "createDate", timeUtil.nowString(),
                "status", ArtStatusType.NORMAL
        );
        redisTemplate.opsForHash().putAll(redisKeyUtil.cont(id), contMap);
        redisTemplate.opsForZSet().add(redisKeyUtil.contSet(parentId), id, getRevTimeScore());
    }

    public void deleteContent(ContentDTO contentDTO) {
        redisTemplate.opsForHash().put(redisKeyUtil.cont(contentDTO.getId()), "status", ArtStatusType.DELETED);
    }

    public void likeContent(ContentDTO contentDTO) throws Exception {
        if(Strings.isBlank(contentDTO.getLikeUserId())) throw new IllegalStateException("LikeUserId can't be blank");
        if(Strings.isBlank(contentDTO.getId())) throw new IllegalStateException("Content id can't be blank");

        RLock lock = redisson.getLock(redisKeyUtil.LIKE_CONT_LOCK);
        String userLike = redisKeyUtil.userLike(contentDTO.getLikeUserId(), contentDTO.getId());
        try{
            if(lock.tryLock()){
                if(!redisTemplate.hasKey(userLike)){
                    redisTemplate.opsForValue().set(userLike, 1);
                    redisTemplate.opsForValue().increment(redisKeyUtil.likeCount(contentDTO.getId()));
                }
            }else{
                log.error("Not get key when like content, userId:contentId={}", userLike);
            }
        } catch (Exception e) {
            log.error("Like content failed, userId:contentId={}", userLike, e);
            throw new Exception(e);
        } finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()){
                lock.unlock();//已鎖定且為當前線程的鎖, 才解鎖
            }
        }
    }
    public void unlikeContent(ContentDTO contentDTO) throws Exception {
        if(Strings.isBlank(contentDTO.getLikeUserId())) throw new IllegalStateException("LikeUserId can't be blank");
        if(Strings.isBlank(contentDTO.getId())) throw new IllegalStateException("Content id can't be blank");

        RLock lock = redisson.getLock(redisKeyUtil.LIKE_CONT_LOCK);
        String userLike = redisKeyUtil.userLike(contentDTO.getLikeUserId(), contentDTO.getId());
        try{
            if(lock.tryLock()){
                if(redisTemplate.hasKey(userLike)){
                    redisTemplate.delete(userLike);
                    redisTemplate.opsForValue().decrement(redisKeyUtil.likeCount(contentDTO.getId()));
                }
            }else{
                log.error("Not get key when unlike content, userId:contentId={}", userLike);
            }
        } catch (Exception e) {
            log.error("Unlike content failed, userId:contentId={}", userLike, e);
            throw new Exception(e);
        } finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()){
                lock.unlock();//已鎖定且為當前線程的鎖, 才解鎖
            }
        }
    }


    /**
     * Reverse currentTimeMillis for redis ZSet, reduce redis time from O(log n) to O(n) when add artSet to ZSet
     * @return
     */
    private long getRevTimeScore(){
        return Long.MAX_VALUE - System.currentTimeMillis();
    }



}

