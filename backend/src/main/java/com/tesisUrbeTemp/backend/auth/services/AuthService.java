package com.tesisUrbe.backend.auth.services;

import com.tesisUrbe.backend.auth.jwt.JwtUtil;
import com.tesisUrbe.backend.users.model.User;
import com.tesisUrbe.backend.users.repository.UserRepository;
import com.tesisUrbe.backend.users.services.UserService;
import com.tesisUrbe.backend.users.utils.UserUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
    private final UserService userService;
    private static final int MAX_ATTEMPTS = 3;
    private final UserRepository userRepository;

    public AuthService(
            JwtUtil jwtUtil,
            AuthenticationManagerBuilder authenticationManagerBuilder,
            UserService userService, UserRepository userRepository, UserUtils userUtils
    ) {
        this.jwtUtil = jwtUtil;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public String authenticate(String userName, String password) {
        int attempt = failedAttempts.getOrDefault(userName, 0);
        if (attempt >= MAX_ATTEMPTS) {
            userService.lockUserAccount(userService.getUserIdByUserName(userName));
            throw new LockedException("Cuenta bloqueada por demasiados intentos fallidos");
        }
        try {
            User user = userRepository.findByUserName(userName)
                    .orElseThrow(() -> new BadCredentialsException("Credenciales incorrectas"));
            UserUtils.checkBlock(user);
            var authToken = new UsernamePasswordAuthenticationToken(userName, password);
            var authResult = authenticationManagerBuilder.getObject().authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authResult);
            failedAttempts.remove(userName);
            return jwtUtil.generateToken(authResult);
        } catch (BadCredentialsException e) {
            failedAttempts.put(userName, attempt + 1);
            throw new BadCredentialsException("Credenciales incorrectas");
        }
    }

    public Map<String, String> errorMap(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }

    public String randomToken() {
        return java.util.UUID.randomUUID().toString();
    }

}
