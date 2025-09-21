package com.tesisUrbe.backend.email.controller;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.email.dto.UserRecoveryDto;
import com.tesisUrbe.backend.email.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/email")
public class EmailController {

    private final EmailService emailService;

    // 🔐 Solicitud de recuperación de contraseña
    @PostMapping("/public/password-recovery-request")
    public ResponseEntity<ApiResponse<Void>> passwordRecoveryRequest(
            @Valid @RequestBody UserRecoveryDto dto
    ) {
        var response = emailService.passwordRecoveryRequest(dto);
        return ResponseEntity.status(response.meta().status()).body(response);
    }

    // 🔐 Solicitud de recuperación de cuenta bloqueada
    @PostMapping("/public/account-recovery-request")
    public ResponseEntity<ApiResponse<Void>> accountRecoveryRequest(
            @Valid @RequestBody UserRecoveryDto dto
    ) {
        var response = emailService.accountRecoveryRequest(dto);
        return ResponseEntity.status(response.meta().status()).body(response);
    }

    // 🔐 Solicitud de verificación de correo
    @PostMapping("/email-verification-request/{id}")
    public ResponseEntity<ApiResponse<Void>> emailVerificationRequest(@PathVariable Long id) {
        var response = emailService.emailVerificationRequest(id);
        return ResponseEntity.status(response.meta().status()).body(response);
    }

    // 🔐 Confirmación de recuperación de contraseña
    @PutMapping("/public/password-recovery/{id}/{token}")
    public ResponseEntity<ApiResponse<Void>> passwordRecovery(
            @PathVariable Long id,
            @PathVariable String token,
            @RequestBody String password
    ) {
        var response = emailService.passwordRecovery(id, token, password);
        return ResponseEntity.status(response.meta().status()).body(response);
    }

    // 🔐 Confirmación de recuperación de cuenta bloqueada
    @PutMapping("/public/account-recovery/{id}/{token}")
    public ResponseEntity<ApiResponse<Void>> accountRecovery(
            @PathVariable Long id,
            @PathVariable String token,
            @RequestBody String password // o @RequestBody PasswordDto dto
    ) {
        var response = emailService.accountRecovery(id, token, password);
        return ResponseEntity.status(response.meta().status()).body(response);
    }

    @PutMapping("/public/email-verification/{id}/{token}")
    public ResponseEntity<ApiResponse<Void>> emailVerification(
            @PathVariable Long id,
            @PathVariable String token
    ) {
        var response = emailService.emailVerification(id, token);
        return ResponseEntity.status(response.meta().status()).body(response);
    }

}
