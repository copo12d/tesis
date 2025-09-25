package com.tesisUrbe.backend.auth.services;

import com.tesisUrbe.backend.auth.dto.JwtResponse;
import com.tesisUrbe.backend.auth.jwt.JwtTokenProvider;
import com.tesisUrbe.backend.auth.repository.BlackListedTokenRepository;
import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.entities.account.BlacklistedToken;
import com.tesisUrbe.backend.usersManagement.dto.AuthUserProjection;
import com.tesisUrbe.backend.usersManagement.exceptions.RoleNotFoundException;
import com.tesisUrbe.backend.usersManagement.exceptions.UserNotFoundException;
import com.tesisUrbe.backend.usersManagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final BlackListedTokenRepository blackListedTokenRepository;
    private final UserService userService;
    private final ApiErrorFactory errorFactory;

    private final ConcurrentHashMap<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 3;

    public ApiResponse<JwtResponse> authenticate(String userName, String password) {
        int intentos = failedAttempts.getOrDefault(userName, 0);

        var authUser = userService.findAuthUserProjectionByUserName(userName).orElse(null);

        if (authUser == null) {
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("USER_NOT_FOUND", null, "Usuario no encontrado"))
            );
        }

        if (authUser.isDeleted()) {
            return errorFactory.build(
                    HttpStatus.GONE,
                    List.of(new ApiError("ACCOUNT_DELETED", null, "La cuenta ha sido eliminada"))
            );
        }

        if (authUser.isUserLocked()) {
            return errorFactory.build(
                    HttpStatus.FORBIDDEN,
                    List.of(new ApiError("USER_LOCKED", null, "Tu acceso fue bloqueado por un administrador"))
            );
        }

        if (authUser.isAccountLocked()) {
            return errorFactory.build(
                    HttpStatus.LOCKED,
                    List.of(new ApiError("ACCOUNT_LOCKED", null, "La cuenta está bloqueada por intentos fallidos"))
            );
        }

        if (intentos >= MAX_ATTEMPTS) {
            userService.lockAccount(userName);
            failedAttempts.remove(userName);
            return errorFactory.build(
                    HttpStatus.LOCKED,
                    List.of(new ApiError("ACCOUNT_LOCKED", null, "Cuenta bloqueada por demasiados intentos fallidos"))
            );
        }

        try {
            var authToken = new UsernamePasswordAuthenticationToken(userName, password);
            var authResult = authenticationManagerBuilder.getObject().authenticate(authToken);

            SecurityContextHolder.getContext().setAuthentication(authResult);
            failedAttempts.remove(userName);

            String accessToken = jwtTokenProvider.generateToken(authResult);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authResult);
            Instant expiresAt = jwtTokenProvider.getExpirationFromToken(accessToken);

            JwtResponse jwtResponse = JwtResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresAt(expiresAt)
                    .tokenType("Bearer")
                    .build();

            return new ApiResponse<>(errorFactory.buildMeta(HttpStatus.OK, "Autenticación exitosa"), jwtResponse, null);

        } catch (BadCredentialsException e) {
            failedAttempts.put(userName, intentos + 1);
            return errorFactory.build(
                    HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("INVALID_CREDENTIALS", null, "Credenciales incorrectas"))
            );
        }
    }

    public ApiResponse<JwtResponse> refreshToken(String refreshToken) {
        if (jwtTokenProvider.isTokenExpired(refreshToken)) {
            return errorFactory.build(
                    HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("REFRESH_TOKEN_EXPIRED", null, "El refresh token ha expirado"))
            );
        }

        String username;
        try {
            username = jwtTokenProvider.extractUserName(refreshToken);
        } catch (Exception e) {
            return errorFactory.build(
                    HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("INVALID_REFRESH_TOKEN", null, "Refresh token inválido"))
            );
        }

        AuthUserProjection authUser = userService.findAuthUserProjectionByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if (authUser.isDeleted()) {
            return errorFactory.build(
                    HttpStatus.GONE,
                    List.of(new ApiError("ACCOUNT_DELETED", null, "La cuenta ha sido eliminada"))
            );
        }

        if (authUser.isUserLocked()) {
            return errorFactory.build(
                    HttpStatus.FORBIDDEN,
                    List.of(new ApiError("USER_LOCKED", null, "Tu acceso fue bloqueado por un administrador"))
            );
        }

        if (authUser.isAccountLocked()) {
            return errorFactory.build(
                    HttpStatus.LOCKED,
                    List.of(new ApiError("ACCOUNT_LOCKED", null, "La cuenta está bloqueada por intentos fallidos"))
            );
        }

        if (authUser.getRoleName() == null || authUser.getRoleName().isBlank()) {
            throw new RoleNotFoundException("El usuario no tiene un rol asignado");
        }

        var userDetails = new org.springframework.security.core.userdetails.User(
                authUser.getUserName(),
                authUser.getPassword(),
                List.of(new SimpleGrantedAuthority(authUser.getRoleName()))
        );

        var authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String newAccessToken = jwtTokenProvider.generateToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication); // opcional: rotar

        Instant expiresAt = jwtTokenProvider.getExpirationFromToken(newAccessToken);

        JwtResponse jwtResponse = JwtResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresAt(expiresAt)
                .tokenType("Bearer")
                .build();

        return new ApiResponse<>(errorFactory.buildMeta(HttpStatus.OK, "Token renovado exitosamente"), jwtResponse, null);
    }

    public ApiResponse<Void> logout(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            return errorFactory.build(
                    HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("INVALID_TOKEN", null, "El token no es válido"))
            );
        }

        if (blackListedTokenRepository.existsByToken(token)) {
            return errorFactory.buildSuccess(HttpStatus.OK, "El token ya fue invalidado previamente");
        }

        String userName = jwtTokenProvider.extractUserName(token);
        Instant expiresAt = jwtTokenProvider.getExpirationFromToken(token);

        BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                .token(token)
                .userName(userName)
                .expiresAt(expiresAt)
                .blacklistedAt(LocalDateTime.now())
                .build();

        blackListedTokenRepository.save(blacklistedToken);
        SecurityContextHolder.clearContext();

        return errorFactory.buildSuccess(HttpStatus.OK, "Sesión cerrada exitosamente");
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

}
