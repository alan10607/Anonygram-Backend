package com.alan10607.redis.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class LockRedisService {
    private final RedissonClient redissonClient;
    private static final long LOCK_SEC = 3600;

    private String getArticleLockName(String id){
        return String.format("lock:art:%s", id);
    }

    private String getContentLockName(String id, int no){
        return String.format("lock:cont:%s:%s", id, no);
    }

    private void lock(String key, Runnable runnable) throws InterruptedException {
        RLock lock = redissonClient.getLock(key);
        try{
            boolean tryLock = lock.tryLock(LOCK_SEC, TimeUnit.SECONDS);
            if(tryLock){
                runnable.run();
            }else{
                Thread.sleep(1000);//Hotspot Invalid, reject request if the query exists
                log.info("Function is lock by the key: {}", key);
                throw new IllegalStateException("System busy for too many requests, please try again later");
            }
        } finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()){
                lock.unlock();//Unlock only if key is locked and belongs to the current thread
            }
        }
    }

    public void lockByArticle(String id, Runnable runnable) throws InterruptedException {
        lock(getArticleLockName(id), runnable);
    }

    public void lockByContent(String id, int no, Runnable runnable) throws InterruptedException {
        lock(getContentLockName(id, no), runnable);
    }

}
