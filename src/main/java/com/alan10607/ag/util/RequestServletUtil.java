package com.alan10607.ag.util;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class RequestServletUtil {
    public static String getFromCookie(HttpServletRequest request, String name){
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(name)){
                return cookie.getValue();
            }
        }
        return null;
    }

    public static String getFromParameter(HttpServletRequest request, String name){
        Map<String, String[]> parameterMap = request.getParameterMap();
        String[] params = parameterMap.get(name);
        if(params != null && params.length > 0 && Strings.isNotBlank(params[0])){
            return params[0];
        }
        return null;
    }
}