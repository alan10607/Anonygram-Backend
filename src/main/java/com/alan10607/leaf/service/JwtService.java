package com.alan10607.leaf.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public interface JwtService {
    String extractUsername(String token);
    String extractEmail(String token);
    Set<String> extractRoles(String token);
    String extractSubject(String token);
    <T> T extractClaims(String token, Function<Claims, T> claimsResolver);
    String createToken(UserDetails userDetails);
    String createToken(String username, String email, List<String> roles, UserDetails userDetails);
    String createToken(Map<String, Object> extraClaim, UserDetails userDetails);
    boolean isTokenValid(String token, UserDetails userDetails);
    boolean isTokenExpired(String token);
}