package com.alan10607.redis.service;

import com.alan10607.leaf.exception.LockInterruptedException;
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
    private static final long MAX_WAIT_MS = 100;
    private static final long KEY_EXPIRE_MS = 3000;

    private String getArticleLockName(String id){
        return String.format("lock:art:%s", id);
    }

    private String getContentLockName(String id, int no){
        return String.format("lock:cont:%s:%s", id, no);
    }

    /**
     * The runnable.run() will not start another thread, it will be the same thread as parent.
     * @param key
     * @param runnable
     */
    private void lock(String key, Runnable runnable) throws LockInterruptedException {
        RLock lock = redissonClient.getLock(key);
        try{
            boolean tryLock = lock.tryLock(MAX_WAIT_MS, KEY_EXPIRE_MS, TimeUnit.MILLISECONDS);
            if(tryLock){
                log.info("Lock function, key: {}", key);
                runnable.run();
            }else{
                Thread.sleep(1000);//Hotspot Invalid, reject request if the query exists
                log.info("Function was locked by the key: {}", key);
                throw new LockInterruptedException("System busy for too many requests, please try again later");
            }
            throw new InterruptedException("E");
        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
            log.error("Lock function interrupt, key={}", key, e);
            throw new LockInterruptedException(String.format(
                    "Request failed because thread interrupt, please try again later"));
        } finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()){
                lock.unlock();//Unlock only if key is locked and belongs to the current thread
            }
        }
    }

    public void lockByArticle(String id, Runnable runnable) {
        lock(getArticleLockName(id), runnable);
    }

    public void lockByContent(String id, int no, Runnable runnable) {
        lock(getContentLockName(id, no), runnable);
    }

}
