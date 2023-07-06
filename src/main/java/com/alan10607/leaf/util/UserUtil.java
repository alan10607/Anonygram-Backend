package com.alan10607.leaf.util;

import com.alan10607.leaf.model.GramUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {

    public static String getAuthUserId () {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();//取得Authentication
        if(auth instanceof UsernamePasswordAuthenticationToken){
            GramUser gramUser = (GramUser) auth.getPrincipal();
            return gramUser.isAnonymousId() ?
                    gramUser.getUsername() :
                    Long.toString(gramUser.getId());
        }
        return "unknownUser";
    }

}