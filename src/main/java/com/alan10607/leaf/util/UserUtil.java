package com.alan10607.leaf.util;

import com.alan10607.leaf.model.LeafUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {

    public static String getAuthUserId () {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();//取得Authentication
        if(auth instanceof UsernamePasswordAuthenticationToken){
            LeafUser leafUser = (LeafUser) auth.getPrincipal();
            return leafUser.isAnonymousId() ?
                    leafUser.getUsername() :
                    Long.toString(leafUser.getId());
        }
        return "unknownUser";
    }

}