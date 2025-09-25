package com.tesisUrbe.backend.auth.controllers;

import com.tesisUrbe.backend.auth.dto.LoginRequest;
import com.tesisUrbe.backend.auth.dto.JwtResponse;
import com.tesisUrbe.backend.auth.dto.RefreshTokenRequest;
import com.tesisUrbe.backend.auth.services.AuthService;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base-path}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ApiErrorFactory errorFactory;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest dto) {
        return ResponseEntity.ok(authService.authenticate(dto.getUserName(), dto.getPassword()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    errorFactory.build(HttpStatus.BAD_REQUEST,
                            List.of(new com.tesisUrbe.backend.common.exception.ApiError(
                                    "INVALID_HEADER", null, "El header Authorization es inválido")))
            );
        }

        String token = authHeader.substring(7);
        return ResponseEntity.ok(authService.logout(token));
    }

}
