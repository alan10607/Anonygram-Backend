package com.ag.domain.config;

import com.ag.domain.model.ForumUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@AllArgsConstructor
@Slf4j
public class AnonymousFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        setAnonymousAuthentication(request);
        filterChain.doFilter(request, response);
    }

    private void setAnonymousAuthentication(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) return;// Already set authentication

        ForumUser anonymousUser = new ForumUser.AnonymousUserBuilder().build();
        AnonymousAuthenticationToken authToken = new AnonymousAuthenticationToken(
                anonymousUser.getId(),
                anonymousUser,
                anonymousUser.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails((request)));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

}