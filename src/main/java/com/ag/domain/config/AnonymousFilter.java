package com.ag.domain.config;

import com.ag.domain.constant.TokenType;
import com.ag.domain.model.ForumUser;
import com.ag.domain.model.JwtToken;
import com.ag.domain.util.CookieUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Configuration
@AllArgsConstructor
@Slf4j
public class AnonymousFilter extends OncePerRequestFilter {
    public static final String ANONYMOUS_ID = "Anonymous-Id";
    public static final long ANONYMOUS_COOKIE_MAX_AGE = 3600;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        setAnonymousAuthentication(request, response);
        filterChain.doFilter(request, response);
    }

    private void setAnonymousAuthentication(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) return;// Already set authentication

        String id = getAnonymousIdTokenFromRequest(request, ANONYMOUS_ID);
        ForumUser user = StringUtils.isBlank(id)
                ? new ForumUser.AnonymousUserBuilder().build()
                : new ForumUser.AnonymousUserBuilder(id).build();

        AnonymousAuthenticationToken anonymousToken = createAnonymousToken(user, request);
        SecurityContextHolder.getContext().setAuthentication(anonymousToken);
        response.addHeader(HttpHeaders.SET_COOKIE,
                CookieUtil.createHttpOnlyCookie(ANONYMOUS_ID, user.getId(), ANONYMOUS_COOKIE_MAX_AGE).toString());
    }

    private String getAnonymousIdTokenFromRequest(HttpServletRequest request, String tokenName) {
        return Optional.ofNullable(getHeaderToken(request, tokenName))
                .orElse(CookieUtil.getCookieValue(request, tokenName));
    }

    private String getHeaderToken(HttpServletRequest request, String tokenName) {
        return Optional.ofNullable((request.getHeader(tokenName)))
                .filter(StringUtils::isNotBlank)
                .orElse(null);
    }

    private AnonymousAuthenticationToken createAnonymousToken(ForumUser anonymousUser, HttpServletRequest request) {
        AnonymousAuthenticationToken authToken = new AnonymousAuthenticationToken(
                anonymousUser.getId(),
                anonymousUser,
                anonymousUser.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails((request)));
        return authToken;
    }

}