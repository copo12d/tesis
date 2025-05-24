package com.TesisUrbe.backend.security.services;

import com.TesisUrbe.backend.security.dto.NewUserDto;
import com.TesisUrbe.backend.security.enums.RoleList;
import com.TesisUrbe.backend.security.exceptions.RoleNotFoundException;
import com.TesisUrbe.backend.security.exceptions.UserAlreadyExistsException;
import com.TesisUrbe.backend.security.jwt.JwtUtil;
import com.TesisUrbe.backend.security.model.Role;
import com.TesisUrbe.backend.security.model.User;
import com.TesisUrbe.backend.security.repository.RoleRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final ConcurrentHashMap<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 3;

    public AuthService(UserService userService, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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

    public void registerUser(NewUserDto newUserDto) {
        if (userService.existByUserName(newUserDto.getUserName())) {
            throw new UserAlreadyExistsException("El nombre de usuario ya existe");
        }

        Role roleUser = roleRepository.findByName(RoleList.ROLE_USER)
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));

        User user = new User(newUserDto.getUserName(), passwordEncoder.encode(newUserDto.getPassword()), roleUser);
        userService.save(user);
    }
}
