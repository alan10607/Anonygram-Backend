package com.ag.domain.util;

import com.ag.domain.constant.UserRole;
import com.ag.domain.exception.base.AnonygramRuntimeException;
import com.ag.domain.model.ForumUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    public static ForumUser getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();// get Authentication
        if(auth instanceof UsernamePasswordAuthenticationToken){
            ForumUser user = (ForumUser) auth.getPrincipal();
            user.setPassword(null);
            return user;
        }
        throw new AnonygramRuntimeException("Authorization failed. Please try to login");
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