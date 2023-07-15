package com.alan10607.auth.config;

import com.alan10607.auth.model.ForumUser;
import com.alan10607.auth.service.JwtService;
import com.alan10607.auth.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Configuration
@Data
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsServices;
    private final UserService userService;
    private static final String AUTHORIZATION_KEY = "Authorization";
    private static final String BEARER = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        try{
            setAuthentication(request);
        }catch (Exception e){
            log.info("JwtFilter fail", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null) return;//already set authentication

        String token = getTokenFromRequest(request);
        if(token == null) return;//token not found

        ForumUser user = getUserDetails(token);
        if(user == null) return;//token invalid

        UsernamePasswordAuthenticationToken authToken = createAuthToken(user, request);
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_KEY);
        if(Strings.isNotBlank(token) && token.length() > BEARER.length() && token.startsWith(BEARER)){
            return token.substring(BEARER.length());
        }

        Map<String, String[]> parameterMap = request.getParameterMap();
        String[] params = parameterMap.get(AUTHORIZATION_KEY);
        if(params != null && params.length > 0 && Strings.isNotBlank(params[0])){
            return params[0];
        }

        return null;
    }

    private ForumUser getUserDetails(String token) {
        return checkIsAnonymous(token) ?
                getAnonymousUser(token) :
                getLoginUser(token);
    }

    private boolean checkIsAnonymous(String token) {
        String email = jwtService.extractEmail(token);
        return Strings.isBlank(email);
    }

    private ForumUser getLoginUser(String token) {
        String email = jwtService.extractEmail(token);
        if(email == null) return null;

        ForumUser user = (ForumUser) userDetailsServices.loadUserByUsername(email);
        if(!jwtService.isTokenValid(token, user)) return null;

        return user;
    }

    private ForumUser getAnonymousUser(String token) {
        String anonymousName = jwtService.extractUsername(token);
        if(anonymousName == null) return null;

        if(jwtService.isTokenExpired(token)) return null;

        return userService.getTempAnonymousUser(anonymousName);
    }

    private UsernamePasswordAuthenticationToken createAuthToken(ForumUser user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails((request)));
        return authToken;
    }
}