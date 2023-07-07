package com.alan10607.leaf.service;

import com.alan10607.leaf.advice.DebugDuration;
import com.alan10607.leaf.dao.ContLikeDAO;
import com.alan10607.leaf.dao.ContentDAO;
import com.alan10607.redis.dto.LikeDTO;
import com.alan10607.leaf.model.ContLike;
import com.alan10607.redis.constant.LikeKeyType;
import com.alan10607.redis.service.LikeRedisService;
import com.alan10607.redis.service.LikeUpdateRedisService;
import com.alan10607.redis.service.ContentRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class LikeService {
    private final ContentRedisService contentRedisService;
    private final LikeRedisService likeRedisService;
    private final LikeUpdateRedisService likeUpdateRedisService;
    private final ContentDAO contentDAO;
    private final ContLikeDAO contLikeDAO;

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
        LikeDTO likeDTO = likeRedisService.get(id, no, userId);
        if(likeDTO.getLikeKeyType() == LikeKeyType.UNKNOWN){
            pullToRedis(id, no, userId);
            likeDTO = likeRedisService.get(id, no, userId);
        }

        if(likeDTO.getLikeKeyType() == LikeKeyType.STATIC){
            likeRedisService.expire(likeDTO.getId(), likeDTO.getNo(), likeDTO.getUserId(), likeDTO.getLikeKeyType());
        }

        return likeDTO.getLike();
    }

    private void pullToRedis(String id, int no, String userId){
        boolean like = contLikeDAO.existsByIdAndNoAndUserId(id, no, userId);
        LikeDTO likeDTO = new LikeDTO(id, no, userId, like, LikeKeyType.STATIC);
        likeRedisService.setWithKeyType(likeDTO);
    }

    /**
     * 取消讚, 若已取消會跳過, 透過lua腳本實現原子操作

     * @return
     * @throws Exception
     */
    public boolean set(LikeDTO likeDTO) {
        boolean isSuccess = likeRedisService.set(likeDTO);
        likeUpdateRedisService.set(likeDTO);
        return isSuccess;
    }


    /**
     * 透過批次將按讚資料存入DB, 除了更新ContLike的按讚資料外, 一併更新Content的likes按讚次數
     * 時間需要 O(contId size * contNo size * userId size)
     * 若批次失敗可以設計成保留資料後重新跑批
     */
    @Transactional
    @DebugDuration
    public int saveLikeToDB() {
        List<ContLike> createList = new ArrayList<>();
        List<ContLike> deleteList = new ArrayList<>();
        Map<LikeDTO, Long> likeCount = new HashMap<>();
        List<LikeDTO> updateDTOs = getUpdateList();

        if(updateDTOs.isEmpty()) {
            log.info("No contLike data to save");
            return updateDTOs.size();
        }

        try{
            collectUpdateListAndCount(updateDTOs, createList, deleteList, likeCount);
            saveToDB(createList, deleteList, likeCount);
            removeCache(updateDTOs);
            log.info("Save contLike succeeded, update cache size={}, createList size={}, deleteList size={}, likeCount size={}",
                    updateDTOs.size(), createList.size(),  deleteList.size(), likeCount.size());
        } catch (Exception e) {
            log.error("Save ContLike to DB failed:", e);
            likeUpdateRedisService.set(updateDTOs);
            throw new RuntimeException(e);
        }

        return updateDTOs.size();
    }

    private List<LikeDTO> getUpdateList(){
        if(likeUpdateRedisService.get().isEmpty()) return new ArrayList<>();

        likeUpdateRedisService.renameToBatch();
        return likeUpdateRedisService.getBatch();
    }

    private void collectUpdateListAndCount(List<LikeDTO> updateDTOs,
                                           List<ContLike> createList,
                                           List<ContLike> deleteList,
                                           Map<LikeDTO, Long> likeCount) {
        updateDTOs.forEach(likeDTO ->
                addToUpdateOrCreateListAndCalculateCount(likeDTO, createList, deleteList, likeCount));
    }

    private void addToUpdateOrCreateListAndCalculateCount(LikeDTO likeDTO,
                                                          List<ContLike> createList,
                                                          List<ContLike> deleteList,
                                                          Map<LikeDTO, Long> likeCount){
        String id = likeDTO.getId();
        int no = likeDTO.getNo();
        String userId = likeDTO.getUserId();
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

    private void saveToDB(List<ContLike> createList, List<ContLike> deleteList, Map<LikeDTO, Long> likeCount) {
        contLikeDAO.saveAll(createList);
        contLikeDAO.deleteAllInBatch(deleteList);
        likeCount.entrySet().forEach(count -> contentDAO.increaseLikes(
                count.getKey().getId(),
                count.getKey().getNo(),
                count.getValue()));
    }

    private void removeCache(List<LikeDTO> updateDTOs) {
        updateDTOs.forEach(likeDTO ->
                likeRedisService.expire(likeDTO.getId(), likeDTO.getNo(), likeDTO.getUserId(), LikeKeyType.NEW));

        updateDTOs.forEach(likeDTO ->
                contentRedisService.delete(likeDTO.getId(), likeDTO.getNo()));

        likeUpdateRedisService.deleteBatch();
    }

}
