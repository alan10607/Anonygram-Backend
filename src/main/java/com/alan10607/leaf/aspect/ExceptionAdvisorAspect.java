package com.alan10607.leaf.aspect;

import com.alan10607.leaf.util.ResponseUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
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

    @Pointcut("@annotation(exceptionAdvisor)")
    public void pointcut(ExceptionAdvisor exceptionAdvisor) {
    }

    @Around("execution(* com.alan10607.*.controller.*.*(..))")
    public ResponseEntity handleException(ProceedingJoinPoint pjp, ExceptionAdvisor exceptionAdvisor) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Class<?> returnType = methodSignature.getReturnType();
        Object res = null;
        try {
            if(returnType == void.class){
                pjp.proceed();
            }else{
                res = pjp.proceed();
            }
        } catch (Throwable e) {
            log.error(e.getMessage());
            return ResponseUtil.err(e);
        }
        return ResponseUtil.ok(res);
    }

}