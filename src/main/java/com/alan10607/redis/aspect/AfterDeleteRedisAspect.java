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

    }

}