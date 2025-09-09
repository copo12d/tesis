package com.tesisUrbe.backend.users.exceptions;

public class BlockedUserException extends RuntimeException {
    public BlockedUserException(String message) {
        super(message);
    }
}
