package com.tesisUrbeTemp2.backend.common.exception;

import java.util.List;

public record ApiResponse<T>(
        ApiMeta meta,
        T data,
        List<ApiError> errors
) {}
