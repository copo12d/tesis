package com.tesisUrbe.backend.users.utils;

import com.tesisUrbe.backend.users.dto.NewUserDto;
import com.tesisUrbe.backend.users.dto.UpdateAdminUserDto;
import com.tesisUrbe.backend.users.dto.UpdatePublicUserDto;
import com.tesisUrbe.backend.users.exceptions.InvalidUserDataException;
import lombok.NoArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

@NoArgsConstructor
public class UserUtils {
    public static void validateRequiredFields(NewUserDto dto) {
        if (dto == null ||
                isBlank(dto.getUserName()) ||
                isBlank(dto.getEmail()) ||
                isBlank(dto.getPassword())) {
            throw new InvalidUserDataException("Todos los campos son obligatorios");
        }
    }

    public static void validateRequiredFields(UpdatePublicUserDto dto) {
        if (dto == null ||
                isBlank(dto.getUserName()) ||
                isBlank(dto.getEmail())) {
            throw new InvalidUserDataException("Usuario y correo son obligatorios");
        }
    }

    public static void validateRequiredFields(UpdateAdminUserDto dto) {
        if (dto == null ||
                isBlank(dto.getUserName()) ||
                isBlank(dto.getEmail())) {
            throw new InvalidUserDataException("Usuario y correo son obligatorios");
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }

        if (password.chars().noneMatch(Character::isUpperCase)) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una letra mayúscula");
        }

        if (password.chars().noneMatch(Character::isLowerCase)) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una letra minúscula");
        }

        if (password.chars().noneMatch(Character::isDigit)) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un número");
        }

        if (password.contains(" ")) {
            throw new IllegalArgumentException("La contraseña no debe contener espacios");
        }

        boolean hasSpecial = password.chars()
                .anyMatch(c -> !Character.isLetterOrDigit(c));
        if (!hasSpecial) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un carácter especial");
        }
    }

    public static String normalizeUsername(String username) {
        return username.trim().toLowerCase();
    }

    public static String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static void validarSuperUsuario(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_SUPERUSER"))) {
            throw new AccessDeniedException("Solo un Super Usuario tiene permiso para realizar esta acción");
        }
    }

    public static void validarAdmin(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities().stream().noneMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPERUSER"))) {
            throw new AccessDeniedException("Solo un Administrador o Super Usuario tiene permiso para realizar esta acción");
        }
    }


}
