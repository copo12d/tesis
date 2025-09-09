package com.tesisUrbeTemp2.backend.common.exception;

import java.time.Instant;

public record ApiMeta(
        String apiVersion,
        int status,
        String requestId,
        Instant timestamp,
        String message
) {}
