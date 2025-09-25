package com.tesisUrbe.backend.emailManagement.service;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.emailManagement.model.EmailVerification;
import com.tesisUrbe.backend.emailManagement.repository.EmailVerificationRepository;
import com.tesisUrbe.backend.entities.account.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final ApiErrorFactory errorFactory;

    // 🧩 Guarda la solicitud de verificación de correo
    public void saveVerificationRequest(User user, String token) {
        EmailVerification verification = EmailVerification.builder()
                .user(user)
                .verification_token(token)
                .expiration_date(LocalDateTime.now().plusHours(1)) // ajustable según tu política
                .build();
        emailVerificationRepository.save(verification);
    }

    // 🔐 Valida el token de verificación
    public ApiResponse<EmailVerification> validateToken(Long userId, String token) {
        EmailVerification request = emailVerificationRepository
                .findLatestByUserId(userId, org.springframework.data.domain.PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);

        if (request == null) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.NOT_FOUND, "No se encontró solicitud"),
                    null,
                    List.of(new ApiError("EMAIL_VERIFICATION_REQUEST_NOT_FOUND", null, "No se encontró solicitud"))
            );
        }

        if (!request.getVerification_token().equals(token)) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.FORBIDDEN, "Token inválido"),
                    null,
                    List.of(new ApiError("INVALID_TOKEN", null, "Token inválido"))
            );
        }

        if (LocalDateTime.now().isAfter(request.getExpiration_date())) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.FORBIDDEN, "Token vencido"),
                    null,
                    List.of(new ApiError("EXPIRED_TOKEN", null, "Token vencido"))
            );
        }

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Token válido"),
                request,
                null
        );
    }
}
