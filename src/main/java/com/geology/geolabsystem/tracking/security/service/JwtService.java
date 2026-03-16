package com.geology.geolabsystem.tracking.security.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public String generateAccessToken(Authentication auth) {
        return buildToken(auth, accessExpiration);
    }

    public String generateRefreshToken(Authentication auth) {
        return buildToken(auth, refreshExpiration);
    }

    private String buildToken(Authentication auth, long expiration) {
        return Jwts.builder()
                .subject(auth.getName()) // .setSubject -> .subject (новый синтаксис)
                .claim("roles", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey()) // JJWT сам поймет, что это HS256, если ключ нужной длины
                .compact();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}
