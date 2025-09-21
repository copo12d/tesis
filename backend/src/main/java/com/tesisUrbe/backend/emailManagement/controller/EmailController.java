package com.tesisUrbe.backend.emailManagement.controller;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.emailManagement.dto.UserRecoveryDto;
import com.tesisUrbe.backend.emailManagement.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/public/password-recovery-request")
    public ResponseEntity<ApiResponse<Void>> passwordRecoveryRequest(
            @Valid @RequestBody UserRecoveryDto dto
    ) {
        ApiResponse<Void> response = emailService.passwordRecoveryRequest(dto);
        return ResponseEntity.status(response.meta().status()).body(response);
    }

    @PostMapping("/public/account-recovery-request")
    public ResponseEntity<ApiResponse<Void>> accountRecoveryRequest(
            @Valid @RequestBody UserRecoveryDto dto
    ) {
        ApiResponse<Void> response = emailService.accountRecoveryRequest(dto);
        return ResponseEntity.status(response.meta().status()).body(response);
    }

    @PostMapping("/email-verification-request/{id}")
    public ResponseEntity<ApiResponse<Void>> emailVerificationRequest(@PathVariable Long id) {
        ApiResponse<Void> response = emailService.emailVerificationRequest(id);
        return ResponseEntity.status(response.meta().status()).body(response);
    }

    @PutMapping("/public/password-recovery/{id}/{token}")
    public ResponseEntity<ApiResponse<Void>> passwordRecovery(
            @PathVariable Long id,
            @PathVariable String token,
            @RequestBody String password
    ) {
        ApiResponse<Void> response = emailService.passwordRecovery(id, token, password);
        return ResponseEntity.status(response.meta().status()).body(response);
    }

    @PutMapping("/public/account-recovery/{id}/{token}")
    public ResponseEntity<ApiResponse<Void>> accountRecovery(
            @PathVariable Long id,
            @PathVariable String token,
            @RequestBody String password
    ) {
        ApiResponse<Void> response = emailService.accountRecovery(id, token, password);
        return ResponseEntity.status(response.meta().status()).body(response);
    }

    @PutMapping("/public/email-verification/{id}/{token}")
    public ResponseEntity<ApiResponse<Void>> emailVerification(
            @PathVariable Long id,
            @PathVariable String token
    ) {
        ApiResponse<Void> response = emailService.emailVerification(id, token);
        return ResponseEntity.status(response.meta().status()).body(response);
    }
}
