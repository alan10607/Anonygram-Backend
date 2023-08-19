package com.alan10607.ag.service.redis.queue;

import com.alan10607.ag.dto.LikeDTO;
import com.alan10607.ag.service.forum.LikeService;
import com.alan10607.ag.service.redis.LockRedisService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@NoArgsConstructor
@Slf4j
public class SaveLikeMessageSubscriber implements MessageListener {
    @Autowired
    private LikeService likeService;

    @Autowired
    private LockRedisService lockRedisService;
    public static Deque<LikeDTO> messageQueue = new LinkedList<>();
    public static final int BATCH_SIZE = 10;


    public void onMessage(Message message, byte[] pattern) {
        LikeDTO likeDTO = LikeDTO.fromMessageString(message.toString());
        messageQueue.offer(likeDTO);
        log.info("Listened save like message {}", message);

        if(messageQueue.size() >= 0){
            startBatch();
        }
    }

    public void startBatch(){
        lockRedisService.lockBySaveLikeQueue(() -> beforeSaveLike());
    }

    /*
             vpeek
      [head] 0 1 2 3 4 [bot]
      *push=>         <=add*
        pop<=         <=offer*
      *poll<=         =>pollLast
     remove<=

 Deque 方法:
     push(E) - 在頭部加入元素
     add(E) - 在尾部加入元素
     offer(E) - 在尾部加入元素, 並返回一個boolean值來表示是否成功
     pop() - 刪除在頭部元素, 若為空則回Expection (=remove())
     poll() - 刪除在頭部元素, 若為空則回null (=peekFirst())
     pollLast - 刪除頭部元素, 若為空則回null
     peek() - 返回隊列的開頭, 若為空則回null (=peekFirst())
     peekLast() - 返回隊列的開頭, 若為空則回null
     descendingIterator() - 以相反的排列返回Deque的所有元素
 */

    private void beforeSaveLike(){
        Deque<LikeDTO> updateQueue = getUpdateQueue();
        try {
            likeService.saveLikeToDB(updateQueue);
        } catch (Exception e) {
            log.error("Save content like to DB failed:", e);
            throw e;
        } finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()){
                lock.unlock();//Unlock only if key is locked and belongs to the current thread
            }
        }
    }

    private Deque<LikeDTO> getUpdateQueue(){
        Deque<LikeDTO> updateQueue = new LinkedList<>();
        int count = 0;
        Iterator<LikeDTO> iterator = messageQueue.iterator();
        while (iterator.hasNext() && count < 10) {
            updateQueue.offer(iterator.next());
            ++count;
        }
        log.info("Get save like update queue, size=%s", updateQueue.size());
        return updateQueue;
    }


    private Deque<LikeDTO> removeMessageQueue(Deque<LikeDTO> updateQueue){
        while (!updateQueue.isEmpty()) {
            if(!updateQueue.poll().equals(messageQueue.poll())){
                log.info("???");
            }
            updateQueue.poll();
            messageQueue.poll();
        }
        log.info("Get save like update queue, size=%s", updateQueue.size());
        return updateQueue;
    }
}