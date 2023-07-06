package com.alan10607.leaf.util;

import com.alan10607.auth.model.ForumUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {

    public static String getAuthUserId () {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();//取得Authentication
        if(auth instanceof UsernamePasswordAuthenticationToken){
            ForumUser forumUser = (ForumUser) auth.getPrincipal();
            return forumUser.isAnonymousId() ?
                    forumUser.getUsername() :
                    Long.toString(forumUser.getId());
        }
        return "unknownUser";
    }

}