package com.alan10607.leaf.config;

import com.alan10607.leaf.constant.AutoUserId;
import com.alan10607.leaf.dto.PostDTO;
import com.alan10607.leaf.model.LeafUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.Base64;

@Aspect
@Component
@AllArgsConstructor
@Slf4j
public class AutoUserIdAspect {
    private HttpSession session;

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
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();//取得Authentication
        if(auth instanceof AnonymousAuthenticationToken){
            String sessionId = session.getId();//HttpSession is thread safe
            String base64Id = Base64.getEncoder().encodeToString(hashTo6Bytes(sessionId.getBytes()));
            postDTO.setUserId(base64Id);
        }else{
            LeafUser leafUser = (LeafUser) auth.getPrincipal();
            postDTO.setUserId(leafUser.getId() == -1 ? leafUser.getUsername() : Long.toString(leafUser.getId()));//id=0表示匿名
            postDTO.setUserName(leafUser.getUsername());
        }

    }

    /**
     * 每 6 bytes循環取xor, 6 bytes透過Base64編碼剛好是8字元
     * @param bytes
     * @return
     */
    public byte[] hashTo6Bytes(byte[] bytes) {
        byte[] base64 = new byte[6];
        for(int i = 0; i < bytes.length; i++)
            base64[i % 6] ^= (bytes[i] & 0xFF);//& 0xFF: 只取8bits

        return base64;
    }

}
