package com.tesisUrbe.backend.emailManagement.service;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.emailManagement.model.PasswordRecovery;
import com.tesisUrbe.backend.emailManagement.repository.PasswordRecoveryRepository;
import com.tesisUrbe.backend.entities.account.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {

    private final PasswordRecoveryRepository passwordRecoveryRepository;
    private final ApiErrorFactory errorFactory;

    public void saveRecoveryRequest(User user, String token) {
        PasswordRecovery recovery = PasswordRecovery.builder()
                .user(user)
                .recovery_token(token)
                .expiration_date(LocalDateTime.now().plusHours(1)) // puedes ajustar la duración aquí
                .build();
        passwordRecoveryRepository.save(recovery);
    }

    public ApiResponse<PasswordRecovery> validateToken(Long userId, String token) {
        PasswordRecovery request = passwordRecoveryRepository.findLatestByUserId(userId);

        if (request == null) {
            return errorFactory.build(HttpStatus.NOT_FOUND,
                    List.of(new ApiError("PASSWORD_RECOVERY_REQUEST_NOT_FOUND", null, "No se encontró solicitud")));
        }

        if (!request.getRecovery_token().equals(token)) {
            return errorFactory.build(HttpStatus.FORBIDDEN,
                    List.of(new ApiError("INVALID_TOKEN", null, "Token inválido")));
        }

        if (LocalDateTime.now().isAfter(request.getExpiration_date())) {
            return errorFactory.build(HttpStatus.FORBIDDEN,
                    List.of(new ApiError("EXPIRED_TOKEN", null, "Token vencido")));
        }

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Token válido"),
                request,
                null
        );
    }
}
