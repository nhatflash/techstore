package com.prm292.techstore.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiration}")
    private long jwtAccessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration}")
    private long jwtRefreshTokenExpirationMs;

    public String generateAccessToken(String username, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("email", email);
        claims.put("role", role);
        claims.put("type", "ACCESS");

        return createToken(username, claims, jwtAccessTokenExpirationMs);
    }

    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("type", "REFRESH");
        return createToken(username, claims, jwtRefreshTokenExpirationMs);
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("username", String.class);
    }

    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("email", String.class);
    }

    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (MalformedJwtException e) {
            logger.error("Jwt token malformed: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Jwt token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Jwt token unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Jwt token empty: {}", e.getMessage());
        } catch (SignatureException e) {
            logger.error("Jwt token signature exception: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("An unknown error occurred when trying to get the claims from token: {}", e.getMessage());
        }
        return null;
    }

    private String createToken(String username, Map<String, Object> claims, long expirationMs) {
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .expiration(new Date())
                .expiration(new Date(new Date().getTime() + expirationMs))
                .signWith(getSecretKey())
                .compact();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
