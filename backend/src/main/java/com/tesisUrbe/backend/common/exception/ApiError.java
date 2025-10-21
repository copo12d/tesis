package com.tesisUrbe.backend.common.exception;

public record ApiError(
        String code,
        String field,
        String message
) {
}
