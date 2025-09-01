package com.tesisUrbe.backend.email.enums;

public enum EmailList {
    PASSWORD_RECOVERY(
            "Recuperación de contraseña",
            "<p>Hola %s</p>" +
                    "<p>Haz clic en el siguiente enlace para recuperar tu contraseña:</p>" +
                    "<p><a href=%s>Recuperar Contraseña</a></p>" +
                    "<p>Si no solicitaste este cambio, por favor ignora este correo.</p>"
    ),
    ACCOUNT_RECOVERY(
            "Recuperación de Cuenta",
            "<p>Hola %s</p>" +
                    "<p>Haz clic en el siguiente enlace para recuperar tu cuenta bloqueada:</p>" +
                    "<p><a href=%s>Desbloquear Cuenta</a></p>" +
                    "<p>Si no esta bloqueada tu cuenta, por favor ignora este correo.</p>"
    ),
    EMAIL_VERIFICATION(
            "Verificación de Email",
            "<p>Hola %s</p>" +
                    "<p>Haz clic en el siguiente enlace para verificar el email, %s, de cuenta:</p>" +
                    "<p><a href=%s>Verificacion de Email</a></p>" +
                    "<p>Si no solicitaste la verificacion de correo, por favor ignora este correo.</p>"
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
