package com.ag.domain.service;

import com.ag.domain.exception.AgValidationException;
import com.ag.domain.model.Like;
import com.ag.domain.repository.LikeRepository;
import com.ag.domain.service.base.CrudServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class LikeService extends CrudServiceImpl<Like> {
    private final LikeRepository likeRepository;

    public Like get(String id, int no, String userId) {
        return this.get(new Like(id, no, userId));
    }

    @Override
    public Like getImpl(Like like) {
        return likeRepository.findById(null).get();
    }

    @Override
    public Like createImpl(Like like) {
        //TODO: need work, update like to article repo
        return likeRepository.save(like);
    }

    @Override
    public Like updateImpl(Like like) {
        //TODO: need work, update like to article repo
        return likeRepository.save(like);
    }

    @Override
    public Like patchImpl(Like like) {
        //TODO: need work, update like to article repo
        return likeRepository.save(like);
    }

    @Override
    public Like deleteImpl(Like like) {
        //TODO: need work, update like to article repo
        return null;
    }

    @Override
    protected void beforeCreate(Like like) {
        validateDataIsSet(like);
        validateHavePermission(like);
    }

    @Override
    protected void beforeUpdateAndPatch(Like like) {
        validateDataIsSet(like);
        validateHavePermission(like);
    }

    @Override
    protected void beforeDelete(Like like) {
        validateDataIsSet(like);
        validateHavePermission(like);
    }

    private void validateDataIsSet(Like like) {
        if (like.getId().isBlank()) {
            throw new AgValidationException("No article id");
        }
        if (like.getNo() == null) {
            throw new AgValidationException("No article no");
        }
        if (like.getUserId().isBlank()) {
            throw new AgValidationException("No user id");
        }
        if (like.getState() == null) {
            throw new AgValidationException("No like state");
        }
    }

    void validateHavePermission(Like like) {

    }
    //
//    private final ContentRedisService contentRedisService;
//    private final LikeRedisService likeRedisService;
//    private final SaveLikeMessagePublisher saveLikeMessagePublisher;
//    private final ContentDAO contentDAO;
//    private final ContLikeDAO contLikeDAO;
//
//    public boolean get(String id, int no, String userId){
//        Boolean like = likeRedisService.get(id, no, userId);
//        if(like == null){
//            pullToRedis(id, no, userId);
//            like = likeRedisService.get(id, no, userId);
//        }
//        likeRedisService.expire(id, no, userId);
//
//        return like;
//    }
//
//    private void pullToRedis(String id, int no, String userId){
//        boolean like = contLikeDAO.existsByIdAndNoAndUserId(id, no, userId);
//        LikeDTO likeDTO = new LikeDTO(id, no, userId, like);
//        likeRedisService.set(likeDTO);
//    }
//
//    /**
//     * Set like, and add to the update list, for batch save to DB later
//     * @param likeDTO
//     * @return
//     */
//    public void set(LikeDTO likeDTO) {
//        likeRedisService.set(likeDTO);
//        saveLikeMessagePublisher.publish(likeDTO.toMessageString());
//    }
//
//
//    /**
//     * Save each like to DB and update likes number of content.
//     * Find different between current redis like status and DB like status, update if different.
//     * If the batch fails, save the data back to the update list in Redis, wait the next batch to restart process.
//     * The time complexity is almost update list size.
//     */
//    @Transactional
//    @DebugDuration
//    public void saveLikeToDB(Queue<LikeDTO> updateQueue) {
//        Map<LikeDTO, Boolean> likeMap = new HashMap<>();
//        Map<LikeDTO, Long> likeCount = new HashMap<>();
//        List<ContLike> createEntities = new ArrayList<>();
//        List<ContLike> deleteEntities = new ArrayList<>();
//
//        try{
//            collectLikeMap(updateQueue, likeMap);
//            collectEntityAndCount(likeMap, createEntities, deleteEntities, likeCount);
//            saveToDB(createEntities, deleteEntities, likeCount);
//            removeCache(likeCount);
//            log.info("Save like to DB succeeded, updateQueue size={}, likeMap size={}, createEntities size={}, deleteEntities size={}, likeCount size={}",
//                    updateQueue.size(), likeMap.size(), createEntities.size(),  deleteEntities.size(), likeCount.size());
//        } catch (Exception e) {
//            log.error("Save like to DB failed:", e);
//            throw e;
//        }
//    }
//
//    protected void collectLikeMap(Queue<LikeDTO> updateQueue, Map<LikeDTO, Boolean> likeMap) {
//        Iterator<LikeDTO> iterator = updateQueue.iterator();
//        while(iterator.hasNext()){
//            LikeDTO likeDTO = iterator.next();
//            likeMap.put(new LikeDTO(likeDTO.getId(), likeDTO.getNo(), likeDTO.getUserId()), likeDTO.getLike());
//        }
//    }
//
//    protected void collectEntityAndCount(Map<LikeDTO, Boolean> likeMap,
//                                       List<ContLike> createEntities,
//                                       List<ContLike> deleteEntities,
//                                       Map<LikeDTO, Long> likeCount) {
//        for(Map.Entry<LikeDTO, Boolean> entry : likeMap.entrySet()){
//            addToUpdateOrCreateEntityAndCalculateCount(entry.getKey(), entry.getValue(), createEntities, deleteEntities, likeCount);
//        }
//    }
//
//    private void addToUpdateOrCreateEntityAndCalculateCount(LikeDTO likeDTO,
//                                                            boolean like,
//                                                            List<ContLike> createEntities,
//                                                            List<ContLike> deleteEntities,
//                                                            Map<LikeDTO, Long> likeCount) {
//        String id = likeDTO.getId();
//        int no = likeDTO.getNo();
//        String userId = likeDTO.getUserId();
//        LikeDTO keyPair = new LikeDTO(id, no);
//        ContLike contLike = contLikeDAO.findByIdAndNoAndUserId(id, no, userId);
//        if(like && contLike == null){
//            createEntities.add(new ContLike(id, no, userId));
//            likeCount.put(keyPair, likeCount.getOrDefault(keyPair, 0L) + 1);
//        }else if(!like && contLike != null){
//            deleteEntities.add(contLike);
//            likeCount.put(keyPair, likeCount.getOrDefault(keyPair, 0L) - 1);
//        }
//    }
//
//    private void saveToDB(List<ContLike> createEntities, List<ContLike> deleteEntities, Map<LikeDTO, Long> likeCount) {
//        contLikeDAO.saveAll(createEntities);
//        contLikeDAO.deleteAllInBatch(deleteEntities);
//        likeCount.forEach((key, value) -> contentDAO.increaseLikes(key.getId(), key.getNo(), value));
//    }
//
//    private void removeCache(Map<LikeDTO, Long> likeCount) {
//        for(Map.Entry<LikeDTO, Long> entry : likeCount.entrySet()){
//            LikeDTO keyPair = entry.getKey();
//            contentRedisService.delete(keyPair.getId(), keyPair.getNo());
//        }
//    }

}
