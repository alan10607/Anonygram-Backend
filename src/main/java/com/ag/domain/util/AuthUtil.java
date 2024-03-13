package com.ag.domain.util;

import com.ag.domain.model.ForumUser;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    public static ForumUser getUser() {
        return  ForumUser.builder().id("3e1527ef-4fcb-4c78-a3ad-d4459f2c7778").username("robot").build();
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();//取得Authentication
//        if(auth instanceof UsernamePasswordAuthenticationToken){
//            ForumUser user = (ForumUser) auth.getPrincipal();
//            user.setPassword(null);
//            return user;
//        }
//        throw new AnonygramIllegalStateException("Authorization failed. Please try to login");
    }

    public static String getUserId() {
        return getUser().getId();
    }

    public static String getUsername() {
        return getUser().getUsername();
    }

    public static boolean checkPermission(String userId) {
        return getUser().getId().equals(userId);
    }
}