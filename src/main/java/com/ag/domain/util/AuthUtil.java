package com.ag.domain.util;

import com.ag.domain.constant.UserRole;
import com.ag.domain.model.ForumUser;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
public class AuthUtil {
    public static ForumUser getUser() {
        return  ForumUser.builder().id("2218fc6a-3de9-425a-9757-1a65a67b8500").username("robot").roles(Collections.singletonList(UserRole.NORMAL)).build();
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

    public static boolean isUserEquals(String userId) {
        return getUser().getId().equals(userId);
    }

    public static boolean isRolesHave(UserRole userRole) {
        return getUser().getRoles().contains(userRole);
    }
}