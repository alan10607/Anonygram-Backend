package com.alan10607.ag.config;

import com.alan10607.ag.model.ForumUser;
import com.alan10607.ag.service.auth.AuthService;
import com.alan10607.ag.service.auth.JwtService;
import com.alan10607.ag.service.auth.UserService;
import com.alan10607.ag.util.HttpUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
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

@Configuration
@Data
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsServices;
    private final UserService userService;
    private final AuthService authService;
    private static final String BEARER = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try{
            setAuthentication(request, response);
        }catch (Exception e){
            log.info("JwtFilter fail: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null) return;//already set authentication

        ForumUser user = getUserFromToken(request, response);
        if(user == null) return;//user not found

        UsernamePasswordAuthenticationToken authToken = createAuthToken(user, request);
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private ForumUser getUserFromToken(HttpServletRequest request, HttpServletResponse response){
        String accessToken = getTokenFromRequest(HttpHeaders.AUTHORIZATION, request);
        if(StringUtils.isNotBlank(accessToken)) return getUserDetails(accessToken);

        String refreshToken = getTokenFromRequest(HttpUtil.REFRESH_TOKEN, request);
        if(StringUtils.isNotBlank(refreshToken)) return resetAccessTokenAndGetUserDetails(refreshToken, response);

        return null;//no accessToken and refreshToken
    }

    private String getTokenFromRequest(String tokenName, HttpServletRequest request) {
        String token = HttpUtil.getFromCookie(request, tokenName);
        if(StringUtils.isNotBlank(token)) {
            return token;
        }

        token = request.getHeader(tokenName);
        if(StringUtils.isNotBlank(token) && token.length() > BEARER.length() && token.startsWith(BEARER)){
            return token.substring(BEARER.length());
        }

        return HttpUtil.getFromParameter(request, tokenName);
    }

    private ForumUser getUserDetails(String token) {
        return checkIsAnonymous(token) ?
                getAnonymousUser(token) :
                getLoginUser(token);
    }

    private ForumUser resetAccessTokenAndGetUserDetails(String refreshToken, HttpServletResponse response){
        ForumUser user = getUserDetails(refreshToken);
        if(user == null) return null;//token invalid

        response.setHeader(HttpHeaders.SET_COOKIE, authService.getAccessAndRefreshCookie(user));

        return user;
    }

    private boolean checkIsAnonymous(String token) {
        String email = jwtService.extractEmail(token);
        return StringUtils.isBlank(email);
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