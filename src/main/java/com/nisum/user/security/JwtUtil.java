package com.nisum.user.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtUtil {

    private final long expirationDelay = 10 * 60 * 1000;
    @Value("${jwt.token.secretKey}")
    private String secretKey;

    public String generateToken(String username, List<GrantedAuthority> authorities) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", authorities);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationDelay))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256).compact();
    }

    public boolean isValidToken(String token, String user) {
        String subject = extractSubject(token);
        return subject.equals(user) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    public String extractSubject(String token) {
        return getPayload(token).getBody().getSubject();
    }

    private JwtParserBuilder jwtParserBuilder() {
        return Jwts.parserBuilder().setSigningKey(getSecretKey());
    }

    private Jws<Claims> getPayload(String token) {
        return jwtParserBuilder().build().parseClaimsJws(token);
    }

    private Date extractExpiration(String token) {
        return getPayload(token).getBody().getExpiration();
    }

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

}
