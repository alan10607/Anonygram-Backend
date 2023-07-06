package com.alan10607.leaf.service;

import com.alan10607.auth.model.ForumUser;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    String extractSubject(String token);
    String extractUsername(String token);
    String extractEmail(String token);
    boolean extractIsAnonymous(String token);
    <T> T extractClaims(String token, Function<Claims, T> claimsResolver);
    String createToken(UserDetails userDetails);
    String createToken(Map<String, Object> extraClaim, UserDetails userDetails);
    String createToken(ForumUser forumUser);
    boolean isTokenValid(String token, UserDetails userDetails);
    boolean isTokenExpired(String token);
}