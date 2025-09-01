package com.tesisUrbe.backend.email.controllers;

import com.tesisUrbe.backend.users.dto.UserRecovery;
import com.tesisUrbe.backend.email.service.EmailService;
import com.tesisUrbe.backend.users.exceptions.BlockedUserException;
import com.tesisUrbe.backend.users.exceptions.InvalidUserDataException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<?> passwordRecovery(
            @Valid @RequestBody
            UserRecovery userRecovery,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Revise sus credenciales"));
        }
        try {
            emailService.passwordRecovery(userRecovery);
            return ResponseEntity.ok(Map.of("message", "Link de recuperacion enviado"));
        } catch (BlockedUserException e){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }

    }

    @PostMapping("/account-recovery")
    public ResponseEntity<?> accountRecover(
            @Valid @RequestBody
            UserRecovery userRecovery,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Revise sus credenciales"));
        }
        try {
            emailService.accountRecovery(userRecovery);
            return ResponseEntity.ok(Map.of("message", "Link de recuperacion enviado"));
        } catch (BlockedUserException e){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (InvalidUserDataException e){
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN','SUPERUSER')")
    @PostMapping("/email-verification")
    public ResponseEntity<?> emailVerification(
            Authentication authentication
    ){
        try{
            emailService.emailVerification(authentication);
            return ResponseEntity.ok(Map.of("message", "Link de verificacion enviado"));

        } catch (BlockedUserException e){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (InvalidUserDataException e){
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PutMapping("/email-request/verified/{id}")
    public ResponseEntity<?> emailVerification(
            @RequestParam(name = "token") String token,
            @PathVariable Long id
    ){
        try{
            emailService.verified(id, token);
            return ResponseEntity.ok(Map.of("message", "Email Verificado"));

        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }


}
