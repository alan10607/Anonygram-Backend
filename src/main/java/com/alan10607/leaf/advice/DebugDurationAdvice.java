package com.alan10607.leaf.advice;

import com.alan10607.leaf.util.ToolUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@AllArgsConstructor
@Slf4j
public class DebugDurationAdvice {

    @Around("execution(* com.alan10607.redis.service.*.*(..)) || @annotation(com.alan10607.leaf.advice.DebugDuration)")
    public Object measureTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return pjp.proceed();
        } finally {
            long end = System.currentTimeMillis();
            log.info("Debug {} duration: {}ms", ToolUtil.getFullFunctionName(pjp), (end - start));
        }
    }

}