package com.example.financetracker.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 🔥 Create JWT with username + userId
    public String generateToken(Long userId, String username) {
        return Jwts.builder()
                .subject(username)           // Spring Security principal
                .claim("userId", userId)      // Our internal ID
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }

    // 🔥 Extract username
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // 🔥 Extract userId
    public Long extractUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    // 🔥 Validate expiration
    public boolean isTokenValid(String token) {
        return getClaims(token)
                .getExpiration()
                .after(new Date());
    }

    // 🔥 Centralized JWT parsing
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
