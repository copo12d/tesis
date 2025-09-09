package com.tesisUrbe.backend.email.model;

public class Email {
    private final String subject;
    private final String message;

    public Email(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }

    public String getSubject(Object... args) {
        return String.format(subject, args);
    }

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
