package com.tesisUrbe.backend.auth.controllers;

import com.tesisUrbe.backend.auth.dto.LoginUserDto;
import com.tesisUrbe.backend.auth.services.AuthService;
import com.tesisUrbe.backend.users.exceptions.BlockedUserException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @Valid @RequestBody LoginUserDto user,
            BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Revise sus credenciales"));
        }
        try {
            String jwt = authService.authenticate(user.getUserName(), user.getPassword());
            return ResponseEntity.ok(Map.of("token", jwt));

        } catch (LockedException e) {
            return ResponseEntity.status(HttpStatus.LOCKED)
                    .body(Map.of("error", e.getMessage()));

        } catch (BadCredentialsException | BlockedUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno" + e));
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
