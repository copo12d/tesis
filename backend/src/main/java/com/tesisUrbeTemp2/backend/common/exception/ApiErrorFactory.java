package com.tesisUrbeTemp2.backend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ApiErrorFactory {

    public <T> ApiResponse<T> build(HttpStatus status, List<ApiError> errors) {
        return new ApiResponse<>(buildMeta(status, null), null, errors);
    }

    public ApiMeta buildMeta(HttpStatus status, String message) {
        return new ApiMeta(
                "1.0",
                status.value(),
                UUID.randomUUID().toString(),
                Instant.now(),
                message
        );
    }

    public ApiResponse<Void> buildSuccess(HttpStatus status, String message) {
        return new ApiResponse<>(buildMeta(status, message), null, null);
    }

    public ApiResponse<Void> buildVoid(HttpStatus status, List<ApiError> errors) {
        return new ApiResponse<>(buildMeta(status, null), null, errors);
    }

}
