package com.tesisUrbe.backend.email.enums;

public enum EmailList {
    PASSWORD_RECOVERY(
            "Recuperación de contraseña",
            "<p>Hola %s</p>" +
                    "<p>Haz clic en el siguiente enlace para recuperar tu contraseña:</p>" +
                    "<p><a href=%s>Recuperar Contraseña</a></p>" +
                    "<p>Si no solicitaste este cambio, por favor ignora este correo.</p>"
    );

    private final String subject;
    private final String message;

    EmailList(String subject, String message) {
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
