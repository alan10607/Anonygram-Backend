package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.constant.ArtStatusType;
import com.alan10607.leaf.dto.PostDTO;
import com.alan10607.leaf.service.ArticleService;
import com.alan10607.leaf.service.ContentService;
import com.alan10607.leaf.service.PostService;
import com.alan10607.leaf.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private ArticleService articleService;
    private ContentService contentService;
    private ContLikeServiceImpl contLikeService;
    private final TimeUtil timeUtil;

    @Bean
    CommandLineRunner run2(PostService postService) {
        return args -> {
            for(int i=0; i<100; i++) {
//                PostDTO postDTO = new PostDTO();
//                postDTO.setId("185ca5d5-b5d6-4131-b15f-085c30afe484");
//                postDTO.setAuthor("alan" + i);
//                postDTO.setTitle("標題拉" + i);
//                postDTO.setWord(i + "回文回文abcabc123回文回文abcabc123回文回文abcabc123回文回文abcabc123回文回文abcabc123回文回文abcabc123回文回文abcabc123123");
//                replyPost(postDTO);
            }
        };
    }



    public List<PostDTO> findLatestPosts(PostDTO postDTO) {
        if(Strings.isBlank(postDTO.getUserId())) throw new IllegalStateException("UserId can't be blank");
        List<String> idList = articleService.findArtSetFromRedis(0, 9);
        postDTO.setIdList(idList);
        return findPosts(postDTO);
    }

    public List<String> findArtSet(){
        return articleService.findArtSetFromRedis(0, 99);
    }

    public List<PostDTO> findPosts(PostDTO postDTO) {
        if(Strings.isBlank(postDTO.getUserId())) throw new IllegalStateException("UserId can't be blank");
        if(postDTO.getIdList() == null || postDTO.getIdList().isEmpty() || postDTO.getIdList().size() > 10) throw new IllegalStateException("IdList size should be 1 ~ 10");

        String userId = postDTO.getUserId();
        List<String> idList = postDTO.getIdList();
        List<PostDTO> artList = articleService.findArticleFromRedis(idList);
        for(PostDTO art : artList){
            art.setContList(contentService.findContentFromRedis(art.getId(), 0, 0, userId));
        }
        return artList;
    }

    public List<PostDTO> openTop(PostDTO postDTO) {
        if(Strings.isBlank(postDTO.getId())) throw new IllegalStateException("Id can't be blank");
        if(Strings.isBlank(postDTO.getUserId())) throw new IllegalStateException("UserId can't be blank");
        if(postDTO.getNo() == null) throw new IllegalStateException("No can't be null");

        String id = postDTO.getId();
        String userId = postDTO.getUserId();
        int openStart = postDTO.getNo() + 1;
        int size = 10;
        return contentService.findContentFromRedis(id, openStart, openStart + size - 1, userId);
    }

    public List<PostDTO> openBot(PostDTO postDTO) {
        if(Strings.isBlank(postDTO.getId())) throw new IllegalStateException("Id can't be blank");
        if(Strings.isBlank(postDTO.getUserId())) throw new IllegalStateException("UserId can't be blank");
        if(postDTO.getNo() == null) throw new IllegalStateException("No can't be null");

        String id = postDTO.getId();
        String userId = postDTO.getUserId();
        int openEnd = postDTO.getNo() - 1;
        int size = 10;
        return contentService.findContentFromRedis(id, openEnd - size + 1, openEnd, userId);
    }

    public void createPost(PostDTO postDTO) {
        if(Strings.isBlank(postDTO.getTitle())) throw new IllegalStateException("Title can't be blank");
        if(Strings.isBlank(postDTO.getAuthor())) throw new IllegalStateException("Author can't be blank");
        if(Strings.isBlank(postDTO.getWord())) throw new IllegalStateException("Word can't be blank");

        String id = UUID.randomUUID().toString();
        LocalDateTime createAndUpdateTime = timeUtil.now();
        articleService.createArtAndCont(id,
                0,
                postDTO.getTitle(),
                postDTO.getAuthor(),
                postDTO.getWord(),
                createAndUpdateTime);

        articleService.deleteArticleFromRedis(id);
        articleService.updateArtSetFromRedis(id, createAndUpdateTime);
    }

    public void replyPost(PostDTO postDTO) {
        if(Strings.isBlank(postDTO.getId())) throw new IllegalStateException("Id can't be blank");
        if(Strings.isBlank(postDTO.getAuthor())) throw new IllegalStateException("Author can't be blank");
        if(Strings.isBlank(postDTO.getWord())) throw new IllegalStateException("Word can't be blank");

        String id = postDTO.getId();
        LocalDateTime updateTime = timeUtil.now();
        int no = articleService.createContAndUpdateArt(id, postDTO.getAuthor(), postDTO.getWord(), updateTime);
        contentService.deleteContentFromRedis(id, no);
        articleService.deleteArticleFromRedis(id);
        articleService.updateArtSetFromRedis(id, updateTime);
    }

    public void deletePost(PostDTO postDTO) {
        if(Strings.isBlank(postDTO.getId())) throw new IllegalStateException("Id can't be blank");

        String id = postDTO.getId();
        articleService.updateArticleStatus(id, ArtStatusType.DELETED);
        articleService.deleteArticleFromRedis(id);
    }

    public void deleteContent(PostDTO postDTO) {
        if(Strings.isBlank(postDTO.getId())) throw new IllegalStateException("Id can't be blank");
        if(postDTO.getNo() == null) throw new IllegalStateException("No can't be null");

        String id = postDTO.getId();
        int no = postDTO.getNo();
        contentService.updateContentStatus(id, no, ArtStatusType.DELETED);
        contentService.deleteContentFromRedis(id, no);
    }

    //TEST to DO below ---
    public void likeContent(PostDTO postDTO) throws Exception {
        if(Strings.isBlank(postDTO.getId())) throw new IllegalStateException("Id can't be blank");
        if(Strings.isBlank(postDTO.getUserId())) throw new IllegalStateException("UserId can't be blank");
        if(postDTO.getNo() == null) throw new IllegalStateException("No can't be null");

        String id = postDTO.getId();
        int no = postDTO.getNo();
        String userId = postDTO.getUserId();
        contentService.findContentFromRedis(id, no, no, userId);//必需確認存在於redis
        boolean isSuccess = contLikeService.UpdateIsLikeFromRedis(id, no, userId);
        if(isSuccess)
            contentService.updateContentLikesFromRedis(id, no, 1);
    }

    public void unlikeContent(PostDTO postDTO) throws Exception {
        if(Strings.isBlank(postDTO.getId())) throw new IllegalStateException("Id can't be blank");
        if(Strings.isBlank(postDTO.getUserId())) throw new IllegalStateException("UserId can't be blank");
        if(postDTO.getNo() == null) throw new IllegalStateException("No can't be null");

        String id = postDTO.getId();
        int no = postDTO.getNo();
        String userId = postDTO.getUserId();
        articleService.findArticleFromRedis(Arrays.asList(id));//必需確認存在於redis
        boolean isSuccess = contLikeService.UpdateUnLikeFromRedis(id, no, userId);
        if(isSuccess)
            contentService.updateContentLikesFromRedis(id, no, -1);
    }


//
//
//    /**
//     * O(contIds * contNo * userId)
//     */
//    public void saveContLikeToDB() throws Exception {
//        //1 找出要更新的資料
//        Set<String> newKeys = redisTemplate.keys(redisKeyUtil.LIKE_NEW_SET_PATTERN);
//        BiFunction<String, String, Set<String>> findNew
//                = (newKey, oldKey) -> redisTemplate.opsForSet().difference(newKey, oldKey);//差集
//        Map<String, List<ContLike>> newMap = getContLikeDiff(newKeys, findNew);
//
//        Set<String> delKeys = redisTemplate.keys(redisKeyUtil.LIKE_DEL_SET_PATTERN);
//        BiFunction<String, String, Set<String>> findDel
//                = (delKey, oldKey) -> redisTemplate.opsForSet().intersect(delKey, oldKey);//交集
//        Map<String, List<ContLike>> delMap = getContLikeDiff(delKeys, findDel);
//
//        saveContLikeToDBTxn(newMap.get("diffList"), delMap.get("diffList"));
//
//
//        for(ContLike contLike : newMap.get("list")) {
//            RLock lock = redisson.getLock(redisKeyUtil.likeLock(contId, no));
//            try {
//                lock.lock();
//                redisTemplate.opsForSet().move(redisKeyUtil.likeNewSet(contLike.getId(), contLike.getNo()),
//                                                contLike.getUserId(),
//                                                redisKeyUtil.likeOldSet(contLike.getId(), contLike.getNo()));
//            } catch (Exception e) {
//                log.error("Get contLikeDiff failed, contId={}, contNo={}", contId, no, e);
//                throw new Exception(e);
//            } finally {
//                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
//                    lock.unlock();//已鎖定且為當前線程的鎖, 才解鎖
//                }
//            }
//        }
//
//    }
//
//
//
//    private Map<String, List<ContLike>> getContLikeDiff(Set<String> keys, BiFunction<String, String, Set<String>> redisMethod) throws Exception {
//        List<ContLike> diffList = new ArrayList<>();
//        List<ContLike> list = new ArrayList<>();
//        for(String key : keys){
//            Object[] temp = redisKeyUtil.getIdAndNoFromLikeSet(key);
//            String contId = (String) temp[0];
//            int no = (int) temp[1];
//            String oldKey = redisKeyUtil.likeOldSet(contId, no);
//            Set<String> diffSet = new HashSet<>();
//            Set<String> set = new HashSet<>();
//
//            RLock lock = redisson.getLock(redisKeyUtil.likeLock(contId, no));
//            try{
//                lock.lock();
//                diffSet = redisMethod.apply(key, oldKey);//要更新的
//                set = redisTemplate.opsForSet().members(key);//做完之後要清空的
//            } catch (Exception e) {
//                log.error("Get contLike diff failed, contId={}, contNo={}", contId, no, e);
//                throw new Exception(e);
//            } finally {
//                if(lock.isLocked() && lock.isHeldByCurrentThread()){
//                    lock.unlock();//已鎖定且為當前線程的鎖, 才解鎖
//                }
//            }
//
//            diffSet.stream().forEach((s) -> diffList.add(new ContLike(contId, no, s)));
//            set.stream().forEach((s) -> list.add(new ContLike(contId, no, s)));
//        }
//
//        return Map.of("diffList", diffList, "list", list);
//    }
//
//    @Transactional
//    public void saveContLikeToDBTxn(List<ContLike> createList, List<ContLike> delList){
//        try{
//            contLikeDAO.saveAll(createList);
//            contLikeDAO.deleteAll(delList);
//        } catch (Exception e) {
//            log.error("Save ContLike transaction failed:", e);
//            throw new RuntimeException(e);
//        }
////    }
//
//    public void saveArtAndContToDB(){
//        TxnParam redisUpdateScore = txnParamDAO.findByKey("redisUpdateScore")
//                .orElse(new TxnParam("redisUpdateScore",
//                        timeUtil.BATCH_START_STR,//2100EpochMilli - 2022EpochMilli
//                        timeUtil.now()
//                ));
//
//        long lastScore = Long.parseLong(redisUpdateScore.getKey());
//
//        //1 找出需要更新的article
//        Set<ZSetOperations.TypedTuple<String>> zSet = redisTemplate.opsForZSet().rangeByScore(redisKeyUtil.ART_SET, 0, lastScore);
//        List<String> idList = zSet.stream()
//                .map(s -> s.getValue())
//                .collect(Collectors.toList());
//
//        //2 查詢已存在article
//        List<Article> artList = articleDAO.findByIdIn(idList);
//        List<Content> contList = new ArrayList<>();
//        Map<String, Article> existMap = artList.stream().collect(Collectors.toMap(Article::getId, a -> a));//O(art)
//
//        //3 整理更新數據, O(allArt * needUpdateCont)
//        for(String id : idList){
//            Map<String, Object> artMap = redisTemplate.opsForHash().entries(redisKeyUtil.art(id));
//            int contNum = ((Number) artMap.get("contNum")).intValue();
//            int startNum = 0;
//            if(existMap.containsKey(id)){
//                //只更新需要修改的部分, 不修改id, title, createDate
//                Article article = existMap.get(id);
//                startNum = article.getContNum() + 1;//從下一個開始更新
//                article.setContNum(contNum);
//                article.setStatus((ArtStatusType) artMap.get("status"));
//                article.setUpdateDate(timeUtil.parseString((String) artMap.get("updateDate")));
//            }else{
//                artList.add(new Article(id,
//                        (String) artMap.get("title"),
//                        contNum,
//                        (ArtStatusType) artMap.get("status"),
//                        timeUtil.parseString((String) artMap.get("updateDate")),
//                        timeUtil.parseString((String) artMap.get("createDate")))
//                );
//            }
//
//            for(int no = startNum; no <= contNum; no++){
//                Map<String, Object> contMap = redisTemplate.opsForHash().entries(redisKeyUtil.cont(id, no));
//                contList.add(new Content(id,
//                        no,
//                        (String) contMap.get("author"),
//                        (String) contMap.get("word"),
//                        ((Number) contMap.get("likes")).longValue(),
//                        ((ArtStatusType) contMap.get("status")).name(),
//                        timeUtil.parseString((String) contMap.get("updateDate")),
//                        timeUtil.parseString((String) contMap.get("createDate"))
//                ));
//            }
//        }
//
//        //4 紀錄下次更新的起點
//        //以一開始查的zSet為準, 假設過程中有新的資料, 就會覆蓋再重複一次, 但結果不變
//        lastScore = zSet.stream().findFirst().get().getScore().longValue();
//        redisUpdateScore.setValue(Long.toString(lastScore));
//        redisUpdateScore.setUpdateDate(timeUtil.now());
//
//        //5 執行事務
//        saveArticleToDBTxn(artList, contList, redisUpdateScore);
//    }
//
//
//    @Transactional
//    public void saveArticleToDBTxn(List<Article> artList, List<Content> contList, TxnParam redisUpdateScore){
//        try{
//            articleDAO.saveAll(artList);
//            contentDAO.saveAll(contList);
//            txnParamDAO.save(redisUpdateScore);
//        } catch (Exception e) {
//            log.error("Save Article and Content transaction failed:", e);
//            throw new RuntimeException(e);
//        }
//    }
//
//
//    public void initArtSetRedis(){
//        List<String> idList = articleDAO.findLatest100Id();
//        for(int i = 0; i < idList.size(); i++)
//            redisTemplate.opsForZSet().add(redisKeyUtil.ART_SET, idList.get(0), timeUtil.BATCH_START + i);
//
//        log.info("Init redis artSet succeeded, cache {} ids to artSet", idList.size());
//    }

}


