package com.alan10607.redis.aspect;

import com.alan10607.leaf.constant.AutoUserId;
import com.alan10607.leaf.dto.PostDTO;
import com.alan10607.leaf.model.LeafUser;
import com.alan10607.leaf.util.ResponseUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@AllArgsConstructor
@Slf4j
public class AfterDeleteRedisAspect {

    @Pointcut("@annotation(afterDeleteRedis)")
    public void pointcut(AfterDeleteRedis afterDeleteRedis) {
    }

    /**
     * 將Session id轉為Base64後放入userId
     * Session 9位數, 2^30=1073741824, int可涵盖之
     * @param jp
     */
    @AfterReturning(pointcut="pointcut(afterDeleteRedis)", returning="res")
    public void deleteRedisCache(JoinPoint jp, Object res, AfterDeleteRedis afterDeleteRedis) {
        if(!autoUserId.enable()) return;

        PostDTO postDTO = (PostDTO) jp.getArgs()[0];
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();//取得Authentication
        if(auth instanceof UsernamePasswordAuthenticationToken){
            LeafUser leafUser = (LeafUser) auth.getPrincipal();

            postDTO.setUserId(leafUser.isAnonymousId() ?
                    leafUser.getUsername() :
                    Long.toString(leafUser.getId()));
            postDTO.setUserName(leafUser.getUsername());
        }
    }
    @Around("execution(* com.alan10607.redis.controller.*.*(..))")
    public Object handleException(ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Class<?> returnType = methodSignature.getReturnType();
        Object res = null;
        try {
            res = pjp.proceed();
        } catch (Throwable e) {
            log.error(e.getMessage());
            return ResponseUtil.err(e);
        }
        return ResponseUtil.ok(res);
    }

}