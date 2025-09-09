package com.tesisUrbeTemp2.backend.common.exception;

public class RoleNotAllowedException extends RuntimeException {
    public RoleNotAllowedException(String message) {
        super(message);
    }
}
