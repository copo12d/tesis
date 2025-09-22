package com.tesisUrbe.backend.emailManagement.service;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.emailManagement.model.AccountRecovery;
import com.tesisUrbe.backend.emailManagement.repository.AccountRecoveryRepository;
import com.tesisUrbe.backend.entities.account.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountRecoveryService {

    private final AccountRecoveryRepository accountRecoveryRepository;
    private final ApiErrorFactory errorFactory;

    public void saveRecoveryRequest(User user, String token) {
        AccountRecovery recovery = AccountRecovery.builder()
                .user(user)
                .recovery_token(token)
                .expiration_date(LocalDateTime.now().plusHours(1)) // ajustable según tu política
                .build();
        accountRecoveryRepository.save(recovery);
    }

    public ApiResponse<AccountRecovery> validateToken(Long userId, String token) {
        AccountRecovery request = accountRecoveryRepository
                .findLatestByUserId(userId, org.springframework.data.domain.PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);

        if (request == null) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.NOT_FOUND, "No se encontró solicitud"),
                    null,
                    List.of(new ApiError("ACCOUNT_RECOVERY_REQUEST_NOT_FOUND", null, "No se encontró solicitud"))
            );
        }

        if (!request.getRecovery_token().equals(token)) {
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
