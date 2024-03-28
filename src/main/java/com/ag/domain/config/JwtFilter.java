package com.ag.domain.config;

import com.ag.domain.constant.TokenType;
import com.ag.domain.exception.AgValidationException;
import com.ag.domain.model.ForumUser;
import com.ag.domain.model.JwtToken;
import com.ag.domain.service.UserService;
import com.ag.domain.util.CookieUtil;
import com.ag.domain.util.ValidationUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
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
public class JwtFilter extends OncePerRequestFilter {
    private final UserService userService;
    private static final String BEARER = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            setAuthentication(request, response);
        } catch (Exception e) {
            log.info("JwtFilter error", e);
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) return;// Already set authentication

        String token = getAccessTokenFromRequest(request, TokenType.ACCESS_TOKEN.header);
        if (StringUtils.isBlank(token)) return;// Token not found

        JwtToken accessToken = new JwtToken.ParseFromTokenBuilder(token).build();
        if (accessToken == null || !isValidAccessToken(accessToken)) return;// Invalid token

        ForumUser user = getUserFromToken(accessToken);
        if (user == null) return;// User not found

        UsernamePasswordAuthenticationToken authToken = createAuthToken(user, request);
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private String getAccessTokenFromRequest(HttpServletRequest request, String tokenName) {
        return Optional.ofNullable(getHeaderToken(request, tokenName))
                .orElse(CookieUtil.getCookieValue(request, tokenName));
    }

    private String getHeaderToken(HttpServletRequest request, String tokenName) {
        return Optional.ofNullable((request.getHeader(tokenName)))
                .filter(jwt -> StringUtils.isNotBlank(jwt) && jwt.length() > BEARER.length() && jwt.startsWith(BEARER))
                .map(jwt -> jwt.substring(BEARER.length()))
                .orElse(null);
    }

    private UsernamePasswordAuthenticationToken createAuthToken(ForumUser user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails((request)));
        return authToken;
    }

    public ForumUser getUserFromToken(JwtToken jwtToken) {
        ForumUser user = userService.get(jwtToken.getUserId());
        if (user == null || !jwtToken.isTokenValid(user)) {
            throw new JwtException(String.format("Invalid token=%s for user=%s", jwtToken.getValue(), jwtToken.getUserId()));
        }
        return user;
    }

    private boolean isValidAccessToken(JwtToken accessToken) {
        try {
            validateAccessToken(accessToken);
            return true;
        } catch (AgValidationException e) {
            log.debug("Validate AccessToken failed: {}", e.getMessage());
            return false;
        }
    }

    void validateAccessToken(JwtToken jwtToken) {
        ValidationUtil.assertTrue(jwtToken.getTokenType() == TokenType.ACCESS_TOKEN, "Not a accessToken");
        ValidationUtil.assertTrue(!jwtToken.isTokenExpired(), "AccessToken is expired ");
    }

    public void validateRefreshToken(JwtToken jwtToken) {
        ValidationUtil.assertTrue(jwtToken.getTokenType() == TokenType.REFRESH_TOKEN, "Not a refreshToken");
        ValidationUtil.assertTrue(!jwtToken.isTokenExpired(), "RefreshToken is expired ");
    }

}