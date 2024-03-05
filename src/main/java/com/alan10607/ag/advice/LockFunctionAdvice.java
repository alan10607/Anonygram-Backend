package com.alan10607.ag.advice;

import com.ag.domain.util.LockUtil;
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
public class LockFunctionAdvice {
    @Pointcut("@annotation(LockFunction)")
    public void LockFunctionMethod() {}

    @Around("LockFunctionMethod()")
    public Object executeWithLock(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return LockUtil.lock("", () -> {
                try {
                    return pjp.proceed();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
        } finally {
            long end = System.currentTimeMillis();
            long duration = end - start;
            if(duration > 1000){
                log.info("Debug {} duration: {}ms", getFullFunctionName(pjp), duration);
            }else{
                log.debug("Debug {} duration: {}ms", getFullFunctionName(pjp), duration);
            }
        }
    }

    private String getFullFunctionName(ProceedingJoinPoint pjp){
        String packageName = pjp.getSignature().getDeclaringTypeName();
        String methodName = pjp.getSignature().getName();
        String argNames = Arrays.stream(pjp.getArgs()).map(Object::getClass)
                .map(Class::getSimpleName)
                .collect(Collectors.joining(","));
        return String.format("%s().%s(%s)", packageName, methodName, argNames);
    }

}