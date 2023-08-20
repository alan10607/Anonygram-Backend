package com.alan10607.ag.service.redis.queue;

import com.alan10607.ag.dto.LikeDTO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.LinkedList;

@Service
@NoArgsConstructor
@Slf4j
public class SaveLikeMessageSubscriber implements MessageListener {
    private static Deque<LikeDTO> messageQueue = new LinkedList<>();

    public void onMessage(Message message, byte[] pattern) {
        LikeDTO likeDTO = LikeDTO.fromMessageString(message.toString());
        messageQueue.offer(likeDTO);
        log.info("Listened save like message {}", message);
    }

    public Deque<LikeDTO> getMessageQueue(){
        return this.messageQueue;
    }

}