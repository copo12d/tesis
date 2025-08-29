package com.tesisUrbe.backend.email.controllers;

import com.tesisUrbe.backend.email.dto.UserPassRecovery;
import com.tesisUrbe.backend.email.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/email-request")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/password-recovery")
    public ResponseEntity<?> recoveryPassword(
            @Valid @RequestBody
            UserPassRecovery userPassRecovery,
            BindingResult bindingResult
    ){
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Revise sus credenciales"));
            }
            emailService.PasswordRecovery(userPassRecovery);
            return ResponseEntity.ok(Map.of("message", "Link de recuperacion enviado"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }

    }

    @ExceptionHandler({AccessDeniedException.class, AuthenticationCredentialsNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleAuthException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autorizado"));
    }

}
