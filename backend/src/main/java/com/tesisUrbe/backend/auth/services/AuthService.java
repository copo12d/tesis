package com.tesisUrbe.backend.auth.services;

import com.tesisUrbe.backend.auth.jwt.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final ConcurrentHashMap<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 3;

    public AuthService(JwtUtil jwtUtil, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.jwtUtil = jwtUtil;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    public String Authenticate(String userName, String password) {
        if (failedAttempts.getOrDefault(userName, 0) >= MAX_ATTEMPTS) {
            throw new LockedException("Cuenta bloqueada por demasiados intentos fallidos");
        }

        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, password);
            Authentication authResult = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authResult);

            failedAttempts.remove(userName);
            return jwtUtil.generateToken(authResult);
        } catch (BadCredentialsException e) {
            failedAttempts.put(userName, failedAttempts.getOrDefault(userName, 0) + 1);
            throw new BadCredentialsException("Credenciales incorrectas");
        }
    }

    public Map<String, String> errorMap(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }

}
