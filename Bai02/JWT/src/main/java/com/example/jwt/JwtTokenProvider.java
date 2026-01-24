package com.example.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final String SECRET_KEY_STRING = "day-la-chuoi-bi-mat-sieu-cap-vu-tru-123456";
    private final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());

    public String createToken(String username, String role, boolean isRefresh) {
        long now = System.currentTimeMillis();
        long validity = isRefresh ? 604800000 : 3600000;

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + validity))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }
}