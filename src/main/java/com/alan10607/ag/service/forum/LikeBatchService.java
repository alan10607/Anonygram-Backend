package com.alan10607.ag.service.forum;

import com.alan10607.ag.dto.LikeDTO;
import com.alan10607.ag.service.redis.LockRedisService;
import com.alan10607.ag.service.redis.queue.SaveLikeMessageSubscriber;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class LikeBatchService {
    private final LikeService likeService;
    private final LockRedisService lockRedisService;
    private final SaveLikeMessageSubscriber saveLikeMessageSubscriber;

    private static List<LikeDTO> updateList = new ArrayList<>();
    private static final int MAX_UPDATE_SIZE = 10;

    public void startBatch(){
        lockRedisService.lockBySaveLikeQueue(() -> prepareUpdateList());
    }

    private void prepareUpdateList(){
        Deque<LikeDTO> messageQueue = saveLikeMessageSubscriber.getMessageQueue();
        List<LikeDTO> updateList = fetchUpdateList(messageQueue);
        if(updateList.isEmpty()){
            log.info("Like updateList is empty, skip batch");
            return;
        }

        likeService.saveLikeToDB(updateList);
        clearUpdateList();
    }

    private List<LikeDTO> fetchUpdateList(Deque<LikeDTO> messageQueue){
        int count = 0;
        while (!messageQueue.isEmpty() && count < MAX_UPDATE_SIZE) {
            updateList.add(messageQueue.poll());
            ++count;
        }
        log.info("Move save like messageQueue to updateList, size={}", updateList.size());
        return updateList;
    }


    private void clearUpdateList(){
        int size = updateList.size();
        updateList.clear();
        log.info("Clear save like updateList, size={}", size);
    }

}
