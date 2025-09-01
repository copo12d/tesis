package com.tesisUrbe.backend.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private final Duration expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public JwtUtil(@Value("${jwt.expiration}") String expiration) {
        this.expiration = parseDuration(expiration);
    }

    private Duration parseDuration(String value) {
        try {
            if (value.matches("\\d+")) {
                return Duration.ofMillis(Long.parseLong(value));
            } else if (value.endsWith("ms")) {
                return Duration.ofMillis(Long.parseLong(value.replace("ms", "")));
            } else if (value.endsWith("s")) {
                return Duration.ofSeconds(Long.parseLong(value.replace("s", "")));
            } else if (value.endsWith("m")) {
                return Duration.ofMinutes(Long.parseLong(value.replace("m", "")));
            } else if (value.endsWith("h")) {
                return Duration.ofHours(Long.parseLong(value.replace("h", "")));
            } else {
                return Duration.parse(value);
            }
        } catch (Exception e) {
            throw new RuntimeException("Formato de expiración de JWT inválido: " + value, e);
        }
    }

    public String generateToken(Authentication authentication) {
        UserDetails mainUser = (UserDetails) authentication.getPrincipal();
        System.out.println(mainUser);
        SecureDigestAlgorithm<SecretKey, ?> algorithm = Jwts.SIG.HS256;
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration.toMillis());

        return Jwts.builder()
                .subject(mainUser.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), algorithm)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new RuntimeException("Error al parsear el token: " + e.getMessage(), e);
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        return userDetails.getUsername().equals(extractUserName(token)) && !isTokenExpired(token);
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractUserName(String token) {
        return extractAllClaims(token).getSubject();
    }
}
