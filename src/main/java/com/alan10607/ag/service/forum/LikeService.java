package com.alan10607.ag.service.forum;

import com.alan10607.ag.advice.DebugDuration;
import com.alan10607.ag.dao.ContLikeDAO;
import com.alan10607.ag.dao.ContentDAO;
import com.alan10607.ag.dto.LikeDTO;
import com.alan10607.ag.model.ContLike;
import com.alan10607.ag.service.redis.LikeRedisService;
import com.alan10607.ag.service.redis.UpdateLikeRedisService;
import com.alan10607.ag.service.redis.ContentRedisService;
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
    private final UpdateLikeRedisService updateLikeRedisService;
    private final ContentDAO contentDAO;
    private final ContLikeDAO contLikeDAO;

    public boolean get(String id, int no, String userId){
        Boolean like = likeRedisService.get(id, no, userId);
        if(like == null){
            pullToRedis(id, no, userId);
            like = likeRedisService.get(id, no, userId);
        }

        if(!updateLikeRedisService.existOrBatchExist(id, no, userId)){
            likeRedisService.expire(id, no, userId);
        }

        return like;
    }

    private void pullToRedis(String id, int no, String userId){
        boolean like = contLikeDAO.existsByIdAndNoAndUserId(id, no, userId);
        LikeDTO likeDTO = new LikeDTO(id, no, userId, like);
        likeRedisService.set(likeDTO);
    }

    /**
     * Set like, and add to the update list, for batch save to DB later
     * @param likeDTO
     * @return
     */
    public boolean set(LikeDTO likeDTO) {
        boolean isSuccess = likeRedisService.set(likeDTO);
        if(isSuccess){
            updateLikeRedisService.set(likeDTO);
        }
        return isSuccess;
    }


    /**
     * Save the content likes from Redis to DB.
     * In addition to updating ContLike, update the likes number of Content at the same time.
     * It will not save to DB if the status is same between Redis and DB.
     * If the batch fails, save the data back to the update list from Redis, wait the next batch to restart process.
     * If the update is successful, set the expired time to the Redis like data, and remove Redis content data.
     * The time complexity is almost update list size.
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
            updateLikeRedisService.set(updateDTOs);
            throw new RuntimeException(e);
        }

        return updateDTOs.size();
    }

    private List<LikeDTO> getUpdateList(){
        if(updateLikeRedisService.get().isEmpty()) return new ArrayList<>();

        updateLikeRedisService.renameToBatch();
        return updateLikeRedisService.getBatch();
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
        LikeDTO keyPair = new LikeDTO(id, no);
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
        likeCount.forEach((key, value) -> contentDAO.increaseLikes(key.getId(), key.getNo(), value));
    }

    private void removeCache(List<LikeDTO> updateDTOs) {
        updateDTOs.forEach(likeDTO ->
                likeRedisService.expire(likeDTO.getId(), likeDTO.getNo(), likeDTO.getUserId()));

        updateDTOs.forEach(likeDTO ->
                contentRedisService.delete(likeDTO.getId(), likeDTO.getNo()));

        updateLikeRedisService.deleteBatch();
    }

}
