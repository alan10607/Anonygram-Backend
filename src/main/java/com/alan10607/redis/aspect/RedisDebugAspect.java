package com.alan10607.redis.aspect;

import com.alan10607.leaf.util.ResponseUtil;
import com.alan10607.leaf.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@AllArgsConstructor
@Slf4j
public class RedisDebugAspect {

    @Around("execution(* com.alan10607.redis.service.impl.*.*(..))")
    public Object measureRedisTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return pjp.proceed();
        } finally {
            long end = System.currentTimeMillis();
            String packageName = pjp.getSignature().getDeclaringTypeName();
            String methodName = pjp.getSignature().getName();
            log.info("Debug {}().{}() duration: {}ms", packageName, methodName, (end - start));
        }
    }

}