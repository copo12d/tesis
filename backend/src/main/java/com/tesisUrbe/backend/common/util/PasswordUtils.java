package com.tesisUrbe.backend.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PasswordUtils {

    public static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una letra mayúscula");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una letra minúscula");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un número");
        }
        if (!password.matches(".*[!@#$%^&*()_-].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un carácter especial");
        }
    }

    public static boolean isValid(String password) {
        try {
            validatePassword(password);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static String encode(String rawPassword, PasswordEncoder encoder) {
        return encoder.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String encodedPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
