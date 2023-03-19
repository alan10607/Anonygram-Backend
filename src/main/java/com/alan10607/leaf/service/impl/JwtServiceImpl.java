package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {
    private final static String SECRET_KEY = "EsPKLbwWNsOtNoifyls3afApQVXy17mQTd+D22Qy5+/MiSV5eFYxEE651nY41mDt";
    private final static String USERNAME = "username";
    private final static String EMAIL = "email";
    private final static String ROLES = "roles";
    private final static int VALID_HOUR = 1;

    public String extractUsername(String token) {
        return (String) extractClaims(token, c -> c.get(USERNAME));
    }

    public String extractEmail(String token) {
        return (String) extractClaims(token, c -> c.get(EMAIL));
    }

    public Set<String> extractRoles(String token) {
        return new HashSet<>((List<String>) extractClaims(token, c -> c.get(ROLES)));
    }

    public String extractSubject(String token){
        return extractClaims(token, Claims::getSubject);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver){
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String createToken(UserDetails userDetails){
        return createToken(new HashMap<String, Object>(), userDetails);
    }

    public String createToken(String username, String email, List<String> roles, UserDetails userDetails){
        return createToken(Map.of(
                USERNAME, username,
                EMAIL, email,
                ROLES, roles
            ), userDetails);
    }

    public String createToken(Map<String, Object> extraClaim, UserDetails userDetails){
        return Jwts.builder()
                .setClaims(extraClaim)
                .setSubject(userDetails.getUsername())//subject=username
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * VALID_HOUR))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaims(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}