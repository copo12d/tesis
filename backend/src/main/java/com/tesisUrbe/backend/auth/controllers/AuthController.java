package com.tesisUrbe.backend.auth.controllers;

import com.tesisUrbe.backend.auth.dto.LoginUserDto;
import com.tesisUrbe.backend.auth.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginUserDto loginUserDto, BindingResult bindingResult) {
        if (bindingResult.hasGlobalErrors()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Revise sus credenciales"));
        }

        try {
            String jwt = authService.Authenticate(loginUserDto.getUserName(), loginUserDto.getPassword());
            return ResponseEntity.ok(Collections.singletonMap("token", jwt));
        } catch (LockedException e) {
            return ResponseEntity.status(HttpStatus.LOCKED).body(Collections.singletonMap("error", e.getMessage()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Error interno"));
        }
    }


    @GetMapping("/check-auth")
    public ResponseEntity<Map<String, String>> checkAuth() {
        return ResponseEntity.ok(Map.of("status", "Autenticado"));
    }

    @ExceptionHandler({AccessDeniedException.class, AuthenticationCredentialsNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleAuthException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autorizado"));
    }

}
