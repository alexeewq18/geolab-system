package com.geology.geolabsystem.tracking.security.service.token;

import com.geology.geolabsystem.tracking.security.entity.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
                .setSubject(auth.getName())
                .claim("roles", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(UserEntity user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getRoles().stream().map(role -> "ROLE_" + role.name()).toList())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}
