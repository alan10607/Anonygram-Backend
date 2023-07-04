package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.dao.ContLikeDAO;
import com.alan10607.leaf.dao.ContentDAO;
import com.alan10607.leaf.dto.LikeDTO;
import com.alan10607.leaf.model.ContLike;
import com.alan10607.leaf.service.TxnService;
import com.alan10607.leaf.util.RedisKeyUtil;
import com.alan10607.redis.constant.LikeKeyType;
import com.alan10607.redis.service.impl.ContLikeRedisService;
import com.alan10607.redis.service.impl.ContLikeUpdateRedisService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class ContLikeService {
    private TxnService txnService;
    private ContentDAO contentDAO;
    private ContLikeDAO contLikeDAO;
    private final RedisTemplate redisTemplate;
    private final DefaultRedisScript getContentLikeScript;
    private final DefaultRedisScript setContentLikeScript;
    private final RedisKeyUtil keyUtil;
    private static final int LIKE = 1;
    private static final int UNLIKE = 0;

    private final ContLikeRedisService contLikeRedisService;
    private final ContLikeUpdateRedisService contLikeUpdateRedisService;

    @Component
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class IdNoUserIdDTO{
        private String id;
        private int no;
    }
    /**
     * 查詢是否已經有這個userId的按讚紀錄
     * 順序: LIKE_NEW > LIKE_BATCH > LIKE_STATIC > DB
     * LIKE_STATIC: 只能由DB放入資料, 唯讀, 禁止別的方法修改
     * LIKE_BATCH: 若剛好在跑批會用這個替代LIKE_NEW, 批次結束後刪除, 新資料從DB重新查詢後放入LIKE_STATIC
     * LIKE_NEW: 有任何異動優先修改這個, 查詢也優先以這個為主, 批次開始時刪除
     * @param id
     * @param no
     * @param userId
     * @return
     */
    public boolean get(String id, int no, String userId){
        LikeDTO likeDTO = contLikeRedisService.get(id, no, userId);
        if(likeDTO.getLikeKeyType() == null){
            pullToRedis(id, no, userId);
        }

        if(likeDTO.getLikeKeyType() == LikeKeyType.STATIC){
            contLikeRedisService.expire(likeDTO);
        }

        return likeDTO.getLike();
    }

    private void pullToRedis(String id, int no, String userId){
        boolean like = contLikeDAO.existsByIdAndNoAndUserId(id, no, userId);
        LikeDTO likeDTO = new LikeDTO(id, no, userId, like, LikeKeyType.STATIC);
        contLikeRedisService.setWithKeyType(likeDTO);
    }

    /**
     * 取消讚, 若已取消會跳過, 透過lua腳本實現原子操作
     * @param id
     * @param no
     * @param userId
     * @return
     * @throws Exception
     */
    public boolean set(String id, int no, String userId, boolean like) {
        boolean isSuccess = contLikeRedisService.set(id, no, userId, like);
        contLikeUpdateRedisService.set(id, no, userId);
        return isSuccess;
    }


    /**
     * 透過批次將按讚資料存入DB, 除了更新ContLike的按讚資料外, 一併更新Content的likes按讚次數
     * 時間需要 O(contId size * contNo size * userId size)
     * 若批次失敗可以設計成保留資料後重新跑批
     */
    public void saveContLikeToDB() {
        List<ContLike> createList = new ArrayList<>();
        List<ContLike> deleteList = new ArrayList<>();
        Map<LikeDTO, Long> likeCount = new HashMap<>();
        List<LikeDTO> updateDTOs = getUpdateKeys();
        if(updateDTOs.isEmpty()) {
            log.info("No contLike data to save");
            return;
        }

        try{
            updateDTOs.forEach(likeDTO -> addToModifiedListAndCount(likeDTO.getId(),
                            likeDTO.getNo(),
                            likeDTO.getUserId(),
                            createList,
                            deleteList,
                            likeCount));

            contLikeDAO.saveAll(createList);
            contLikeDAO.deleteAllInBatch(deleteList);
            likeCount.entrySet().forEach(count -> contentDAO.increaseLikes(
                            count.getKey().getId(),
                            count.getKey().getNo(),
                            count.getValue()));

            updateDTOs.forEach(likeDTO -> contLikeRedisService.expire(likeDTO.getId(),
                            likeDTO.getNo(),
                            likeDTO.getUserId(),
                            LikeKeyType.NEW));


            log.info("Save contLike succeeded, createList size={}, deleteList size={}, likeCount",
                    createList.size(),  deleteList.size(), likeCount.size());
        } catch (Exception e) {
            log.error("Save ContLike to DB failed:", e);
            contLikeUpdateRedisService.set(updateDTOs);
            throw new RuntimeException(e);
        }
    }

    private List<LikeDTO> getUpdateKeys(){
        contLikeUpdateRedisService.renameToBatch();
        return contLikeUpdateRedisService.getBatch();
    }

    private void addToModifiedListAndCount(String id,
                                           int no,
                                           String userId,
                                           List<ContLike> createList,
                                           List<ContLike> deleteList,
                                           Map<LikeDTO, Long> likeCount){
        LikeDTO keyPair = new LikeDTO(id, no, null);
        boolean like = get(id, no, userId);
        ContLike contLike = contLikeDAO.findByIdAndNoAndUserId(id, no, userId);
        if(like && contLike == null){
            createList.add(new ContLike(id, no, userId));
            likeCount.put(keyPair, likeCount.getOrDefault(keyPair, 0L) + 1);
        }else if(!like && contLike != null){
            deleteList.add(contLike);
            likeCount.put(keyPair, likeCount.getOrDefault(keyPair, 0L) - 1);
        }
    }

}
