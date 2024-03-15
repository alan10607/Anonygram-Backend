package com.ag.domain.service;

import com.ag.domain.model.ForumUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
public class JwtService {
    private static final String SECRET_KEY = "EsPKLbwWNsOtNoifyls3afApQVXy17mQTd+D22Qy5+/MiSV5eFYxEE651nY41mDt";
    private static final String COL_NAME_ID = "id";
    private static final String COL_NAME_TOKEN_TYPE = "tokenType";
    private static final String COL_NAME_IS_ANONYMOUS = "isAnonymous";
    private static final long ACCESS_TOKEN_EXPIRED_HOUR = 1;
    private static final long REFRESH_TOKEN_EXPIRED_HOUR = 24 * 30;
    public static final String ACCESS_TOKEN = HttpHeaders.AUTHORIZATION;
    public static final String REFRESH_TOKEN = "Refresh-Token";
    public static final String SET_JWT = "Set-Jwt";

    private enum TokenType {
        ACCESS_TOKEN,
        REFRESH_TOKEN
    }

    public class DefaultTokenBuilder {
        private String id;
        private ForumUser user;
        private TokenType tokenType;
        private long expiredHour;

        public DefaultTokenBuilder setId(String id) {
            this.id = id;
            return this;
        }

        public DefaultTokenBuilder setUser(ForumUser user) {
            this.user = user;
            return this;
        }

        public DefaultTokenBuilder setTokenType(TokenType tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public DefaultTokenBuilder setExpiredHour(long expiredHour) {
            this.expiredHour = expiredHour;
            return this;
        }

        public String build() {
            Map<String, Object> extraClaim = ImmutableMap.<String, Object>builder()
                    .put(COL_NAME_ID, this.id)
                    .put(COL_NAME_TOKEN_TYPE, this.tokenType)
                    .put(COL_NAME_IS_ANONYMOUS, this.user.isAnonymous())
                    .build();

            return createToken(extraClaim, this.user, this.expiredHour);
        }

        private String createToken(Map<String, Object> extraClaim, ForumUser user, long expiredHour) {
            return Jwts.builder()
                    .setClaims(extraClaim)
                    .setSubject(user.getId()) // subject = user id
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * expiredHour))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        }
    }

    public String createAccessToken(ForumUser user) {
        return new DefaultTokenBuilder().setId(user.getId())
                .setUser(user)
                .setTokenType(TokenType.ACCESS_TOKEN)
                .setExpiredHour(ACCESS_TOKEN_EXPIRED_HOUR)
                .build();
    }

    public String createRefreshToken(ForumUser user) {
        return new DefaultTokenBuilder().setId(user.getId())
                .setUser(user)
                .setTokenType(TokenType.REFRESH_TOKEN)
                .setExpiredHour(REFRESH_TOKEN_EXPIRED_HOUR)
                .build();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractSubject(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private Date extractIssuedAt(String token) {
        return extractClaims(token, Claims::getIssuedAt);
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public String extractUserId(String token) {
        return (String) extractClaims(token, c -> c.get(COL_NAME_ID));
    }

    private TokenType extractTokenType(String token) {
        String tokenTypeName = (String) extractClaims(token, c -> c.get(COL_NAME_TOKEN_TYPE));
        return TokenType.valueOf(tokenTypeName);
    }

    public boolean isAccessToken(String token) {
        return extractTokenType(token) == TokenType.ACCESS_TOKEN;
    }

    public boolean isRefreshToken(String token) {
        return extractTokenType(token) == TokenType.REFRESH_TOKEN;
    }

    private Boolean extractIsAnonymous(String token) {
        return (Boolean) extractClaims(token, c -> c.get(COL_NAME_IS_ANONYMOUS));
    }

    public boolean isAnonymous(String token) {
        return BooleanUtils.isTrue()extractIsAnonymous(token);
    }

    public boolean isTokenValid(String token, ForumUser user) {
        String userId = extractUserId(token);
        return (userId.equals(user.getId()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


}