package com.alan10607.ag.util;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class HttpUtil {

    public static String getFromCookie(HttpServletRequest request, String name){
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
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

    public static boolean isMatchPath(String path, String matchPath) {
        return isMatchPath(path, new String[]{ matchPath });
    }

    public static boolean isMatchPath(String path, String[] matchPaths) {
        for(String matchPath : matchPaths){
            if(matchPath.endsWith("/**")){
                int secondSlash = matchPath.indexOf("/", 1);
                String prefix = matchPath.substring(0, secondSlash);
                if(path.startsWith(prefix)) return true;
            }else{
                if(path.equals(matchPath)) return true;
            }
        }
        return false;
    }
}