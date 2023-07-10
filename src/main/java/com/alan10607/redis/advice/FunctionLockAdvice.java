package com.alan10607.redis.advice;

import com.alan10607.leaf.util.RedisKeyUtil;
import com.alan10607.leaf.util.ToolUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Aspect
@Component
@AllArgsConstructor
@Slf4j
public class FunctionLockAdvice {
    private final RedissonClient redissonClient;
    private final RedisTemplate redisTemplate;
    private final RedisKeyUtil redisKeyUtil;
    @Pointcut("@annotation(beforeLock)")
    public void functionLockPointcut(FunctionLock functionLock) {
    }

    @Around("functionLockPointcut(functionLock)")
    public Object functionLocker(ProceedingJoinPoint pjp, FunctionLock functionLock) throws Throwable {
        String key = getKey(functionLock);
        RLock lock = redissonClient.getLock(key);
        try{
            Boolean tryLock = lock.tryLock(functionLock.lockSec(), TimeUnit.SECONDS);
            if(tryLock){
                return pjp.proceed();
            }else{
                Thread.sleep(1000);//Hotspot Invalid, wait for a moment if the query exists
                throw new IllegalStateException(String.format("Lock %s with key: %s", ToolUtil.getFullFunctionName(pjp), key));
            }
        } finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()){
                lock.unlock();//Unlock only if key is locked and belongs to the current thread
            }
        }
    }

    public Object cycle(int time, Supplier<Object> process) throws Exception {

    }


}