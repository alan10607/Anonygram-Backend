package com.alan10607.redis.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.api.RLock;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Data
@NoArgsConstructor
public class ForumRLock {
    private String key;
    private RLock lock;

    public ForumRLock(String key, RLock lock) {
        this.key = getArticleLockName(key);
        this.lock = lock;
    }

    private String getArticleLockName(String id){
        return String.format("lock:art:%s", id);
    }

    public boolean tryLock() throws InterruptedException {
        return lock.tryLock(30, TimeUnit.SECONDS);
    }

    public boolean unlock() {
        if(lock.isLocked() && lock.isHeldByCurrentThread()){
            lock.unlock();//Unlock only if key is locked and belongs to the current thread
            return true;
        }
        return false;
    }
}