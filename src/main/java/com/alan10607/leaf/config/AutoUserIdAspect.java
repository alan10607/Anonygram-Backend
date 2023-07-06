package com.alan10607.leaf.config;

import com.alan10607.auth.service.UserService;
import com.alan10607.leaf.constant.AutoUserId;
import com.alan10607.leaf.dto.PostDTO;
import com.alan10607.auth.model.ForumUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@AllArgsConstructor
@Slf4j
public class AutoUserIdAspect {
    private UserService userService;

    @Pointcut("@annotation(autoUserId)")
    public void autoUserIdPointcut(AutoUserId autoUserId) {
    }

    /**
     * 將Session id轉為Base64後放入userId
     * Session 9位數, 2^30=1073741824, int可涵盖之
     * @param jp
     */
    @Before("autoUserIdPointcut(autoUserId)")
    public void before(JoinPoint jp, AutoUserId autoUserId) {
        if(!autoUserId.enable()) return;

        PostDTO postDTO = (PostDTO) jp.getArgs()[0];
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();//取得Authentication
        if(auth instanceof UsernamePasswordAuthenticationToken){
            ForumUser forumUser = (ForumUser) auth.getPrincipal();

            postDTO.setUserId(forumUser.isAnonymousId() ?
                    forumUser.getUsername() :
                    forumUser.getId());
            postDTO.setUserName(forumUser.getUsername());
        }
    }

}