package com.alan10607.ag.service.forum;

import com.alan10607.ag.dto.LikeDTO;
import com.alan10607.ag.service.redis.LockRedisService;
import com.alan10607.ag.service.redis.queue.SaveLikeMessageSubscriber;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;

@Service
@AllArgsConstructor
@Slf4j
public class LikeBatchService {
    private final LikeService likeService;
    private final LockRedisService lockRedisService;
    private final SaveLikeMessageSubscriber saveLikeMessageSubscriber;

    private static Queue<LikeDTO> updateQueue = new LinkedList<>();
    private static final int MAX_UPDATE_SIZE = 1000;

    public void startBatch(){
        lockRedisService.lockBySaveLikeQueue(() -> prepareUpdateQueue());
    }

    private void prepareUpdateQueue(){
        fetchUpdateQueue();
        if(updateQueue.isEmpty()){
            log.info("Like updateQueue is empty, skip batch");
            return;
        }

        likeService.saveLikeToDB(updateQueue);
        clearUpdateQueue();
    }

    private void fetchUpdateQueue(){
        Queue<LikeDTO> messageQueue = saveLikeMessageSubscriber.getMessageQueue();
        while (!messageQueue.isEmpty() && updateQueue.size() < MAX_UPDATE_SIZE) {
            updateQueue.offer(messageQueue.poll());
        }
    }


    private void clearUpdateQueue(){
        updateQueue.clear();
    }

}
