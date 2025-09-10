package com.tesisUrbe.backend.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesisUrbe.backend.auth.repository.BlackListedTokenRepository;
import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.users.dto.AuthUserProjection;
import com.tesisUrbe.backend.users.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final ApiErrorFactory errorFactory;
    private final ObjectMapper objectMapper;
    private final BlackListedTokenRepository blackListedTokenRepository;

    /**
     * Excluye rutas públicas del filtro JWT.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth") || path.equals("/api/v1/users/public/register");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            sendUnauthorized(response, "Token ausente o mal formado");
            return;
        }

        String token = header.substring(7);

        if (blackListedTokenRepository.existsByToken(token)) {
            sendUnauthorized(response, "El token ha sido revocado");
            return;
        }

        try {
            Claims claims = jwtTokenProvider.extractAllClaims(token);
            String username = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);

            AuthUserProjection authUser = userService.findAuthUserProjectionByUserName(username)
                    .orElse(null);

            if (authUser == null || Boolean.TRUE.equals(authUser.isDeleted())) {
                sendUnauthorized(response, "La cuenta ha sido eliminada");
                return;
            }
            if (Boolean.TRUE.equals(authUser.isUserLocked())) {
                sendUnauthorized(response, "El acceso fue bloqueado por un administrador");
                return;
            }
            if (Boolean.TRUE.equals(authUser.isAccountLocked())) {
                sendUnauthorized(response, "La cuenta está bloqueada por intentos fallidos");
                return;
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(username);

                if (jwtTokenProvider.validateToken(token, userDetails)) {
                    List<GrantedAuthority> authorities = (roles != null)
                            ? roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                            : userDetails.getAuthorities().stream().collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    sendUnauthorized(response, "Token inválido");
                    return;
                }
            }
        } catch (ExpiredJwtException e) {
            sendUnauthorized(response, "Token expirado");
            return;
        } catch (RuntimeException e) {
            sendUnauthorized(response, "Token inválido o manipulado");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        ApiResponse<Void> body = errorFactory.build(
                org.springframework.http.HttpStatus.UNAUTHORIZED,
                List.of(new ApiError("UNAUTHORIZED", null, message))
        );
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
