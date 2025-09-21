package com.tesisUrbe.backend.email.controller;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.email.dto.UserRecoveryDto;
import com.tesisUrbe.backend.email.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/email")
public class EmailController {

    private final EmailService emailService;

    //Post
    @PostMapping("/public/password-recovery-request")
    public ResponseEntity<ApiResponse<Void>> passwordRecoveryRequest(
           @Valid @RequestBody
           UserRecoveryDto userRecoveryDto
    ){
        ApiResponse<Void> response = emailService.passwordRecoveryRequest(userRecoveryDto);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @PostMapping("/public/account-recovery-request")
    public ResponseEntity<ApiResponse<Void>> accountRecoveryRequest(
            @Valid @RequestBody
            UserRecoveryDto userRecoveryDto
    ){
        ApiResponse<Void> response = emailService.accountRecoveryRequest(userRecoveryDto);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @PostMapping("/email-verification-request/{id}")
    public  ResponseEntity<ApiResponse<Void>> emailVerificationRequest(@PathVariable Long id){
        ApiResponse<Void> response = emailService.emailVerificationRequest(id);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    //Put
    @PutMapping("/public/password-recovery/{id}/{token}")
    public ResponseEntity<ApiResponse<Void>> passwordRecovery(
            @PathVariable Long id,
            @PathVariable String token,
            @RequestBody
            String password
    ){
        ApiResponse<Void> response = emailService.passwordRecovery(id, token, password);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @PutMapping("/public/account-recovery/{id}/{token}")
    public ResponseEntity<ApiResponse<Void>> accountRecovery(
            @PathVariable Long id,
            @PathVariable String token,
            @RequestBody
            String password
    ){
        ApiResponse<Void> response = emailService.accountRecovery(id, token, password);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @PutMapping("/public/email-verification/{id}/{token}")
    public ResponseEntity<ApiResponse<Void>> emailVerification(
            @PathVariable Long id,
            @PathVariable String token
    ){
        ApiResponse<Void> response = emailService.emailVerification(id, token);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

}
