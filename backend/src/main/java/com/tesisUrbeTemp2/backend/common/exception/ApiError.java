package com.tesisUrbeTemp2.backend.common.exception;

public record ApiError(
        String code,
        String field,
        String message
) {}
