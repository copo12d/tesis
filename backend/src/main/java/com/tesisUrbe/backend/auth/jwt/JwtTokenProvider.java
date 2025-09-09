package com.tesisUrbe.backend.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final Duration expiration;
    private final Duration refreshExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") String expiration,
            @Value("${jwt.refresh-expiration:7d}") String refreshExpiration
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = parseDuration(expiration);
        this.refreshExpiration = parseDuration(refreshExpiration);
    }

    private Duration parseDuration(String value) {
        try {
            if (value.matches("\\d+")) return Duration.ofMillis(Long.parseLong(value));
            if (value.endsWith("ms")) return Duration.ofMillis(Long.parseLong(value.replace("ms", "")));
            if (value.endsWith("s")) return Duration.ofSeconds(Long.parseLong(value.replace("s", "")));
            if (value.endsWith("m")) return Duration.ofMinutes(Long.parseLong(value.replace("m", "")));
            if (value.endsWith("h")) return Duration.ofHours(Long.parseLong(value.replace("h", "")));
            if (value.endsWith("d")) return Duration.ofDays(Long.parseLong(value.replace("d", "")));
            return Duration.parse(value);
        } catch (Exception e) {
            throw new RuntimeException("Formato de expiración de JWT inválido: " + value, e);
        }
    }

    public String generateToken(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration.toMillis());

        // Solo un rol
        String role = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("El usuario no tiene rol asignado"));

        return Jwts.builder()
                .subject(principal.getUsername())
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration.toMillis());

        return Jwts.builder()
                .subject(principal.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Instant getExpirationFromToken(String token) {
        return extractAllClaims(token).getExpiration().toInstant();
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
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

    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public String extractUserName(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }
}
