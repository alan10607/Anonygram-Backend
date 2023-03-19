package com.alan10607.leaf.config;

import com.alan10607.leaf.constant.LeafRoleType;
import com.alan10607.leaf.service.JwtService;
import com.alan10607.leaf.service.UserService;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final static String BEARER = "Bearer ";
    private final JwtService jwtService;
    private final UserDetailsService userDetailsServices;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        UserDetails userDetails = getUserDetails(request);
        if(userDetails != null) setAuthentication(request, userDetails);

        filterChain.doFilter(request, response);
    }

    private UserDetails getUserDetails(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith(BEARER)) return null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();//取得Authentication
        if(auth != null) return null;

        String jwt = authHeader.substring(BEARER.length());
        if(jwtService.isTokenExpired(jwt)) return null;

        if(jwtService.extractRoles(jwt).contains(LeafRoleType.ANONY.name())){
            String anonyName = jwtService.extractUsername(jwt);
            return userService.getAnonyUser(anonyName);
        }

        String email = jwtService.extractEmail(jwt);
        if(email == null) return null;

        UserDetails userDetails = userDetailsServices.loadUserByUsername(email);
        if(!jwtService.isTokenValid(jwt, userDetails)) return null;

        return userDetails;
    }

    private void setAuthentication(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails((request)));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

}