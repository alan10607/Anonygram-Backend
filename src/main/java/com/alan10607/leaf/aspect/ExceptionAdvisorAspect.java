package com.alan10607.leaf.aspect;

import com.alan10607.leaf.util.ResponseUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@AllArgsConstructor
@Slf4j
public class ExceptionAdvisorAspect {
//
//    @Around("execution(* com.alan10607.redis.controller.*.*(..)) "
//        + "|| execution(* com.alan10607.leaf.controller.ForumController.*(..))")
//    public Object handleException(ProceedingJoinPoint pjp) throws Throwable {
//        MethodSignature signature = (MethodSignature) pjp.getSignature();
//        Class<?> returnType = signature.getReturnType();
//        Object res = null;
//        try {
//            if(returnType == void.class){
//                pjp.proceed();
//            }else{
//                res = pjp.proceed();
//            }
//        } catch (Throwable e) {
//            log.error(e.getMessage());
//            throw e;
//        }
//        return ResponseUtil.ok(res);
//    }

}