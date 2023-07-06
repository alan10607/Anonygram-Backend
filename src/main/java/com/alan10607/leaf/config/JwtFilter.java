package com.alan10607.leaf.config;

import com.alan10607.leaf.model.GramUser;
import com.alan10607.leaf.service.JwtService;
import com.alan10607.leaf.service.UserService;
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
import java.util.Optional;

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
            log.info("JwtFilter fail: " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null) return;//已經setAuthentication

        String token = getTokenFromRequest(request);
        if(token == null) return;//token不存在

        GramUser user = getUserDetails(token);
        if(user == null) return;//token無效

        UsernamePasswordAuthenticationToken authToken = createAuthToken(user, request);
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        Optional<String> headerToken = Optional.ofNullable(request.getHeader(AUTHORIZATION_KEY))
                .filter(Strings::isNotBlank);

         if (headerToken.isPresent()) {
            return headerToken.get();
        }

        return Optional.ofNullable(request.getParameterMap())
                .map(map -> map.get(AUTHORIZATION_KEY))
                .map(arr -> arr[0])
                .filter(str -> Strings.isNotBlank(str))
                .orElse(null);
    }


    private GramUser getUserDetails(String token) {
        return jwtService.extractIsAnonymous(token) ?
                getAnonymousUser(token) :
                getLoginUser(token);
    }

    private GramUser getLoginUser(String token) {
        String email = jwtService.extractEmail(token);
        if(email == null) return null;

        GramUser user = (GramUser) userDetailsServices.loadUserByUsername(email);
        if(!jwtService.isTokenValid(token, user)) return null;

        return user;
    }

    private GramUser getAnonymousUser(String token) {
        if(jwtService.isTokenExpired(token)) return null;

        String anonymousName = jwtService.extractUsername(token);
        if(anonymousName == null) return null;

        return userService.getAnonymousUser(anonymousName);
    }

    private UsernamePasswordAuthenticationToken createAuthToken(GramUser user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails((request)));
        return authToken;
    }
}