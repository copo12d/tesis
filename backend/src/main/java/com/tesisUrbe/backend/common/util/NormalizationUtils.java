package com.tesisUrbe.backend.common.util;

import com.tesisUrbe.backend.solidWasteManagement.enums.BatchStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NormalizationUtils {

    public static String normalize(String input, boolean toLowerCase) {
        if (input == null) {
            return null;
        }
        String normalized = input.trim();
        normalized = normalized.replaceAll("\\s+", " ");
        if (toLowerCase) {
            normalized = normalized.toLowerCase();
        }
        return normalized;
    }

    public static String normalizeUsername(String username) {
        return normalize(username, true);
    }

    public static String normalizeEmail(String email) {
        return normalize(email, true);
    }

    public static String normalizeText(String text) {
        return normalize(text, false);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null
                ? dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : "";
    }

    public static String translateStatus(BatchStatus status) {
        if (status == null) return "";
        return switch (status) {
            case IN_PROGRESS -> "En progreso";
            case PROCESSED -> "Procesado";
        };
    }

    public static String translateRole(String role) {
        return switch (role) {
            case "ROLE_ADMIN" -> "Administrador";
            case "ROLE_SUPERUSER" -> "Superusuario";
            case "ROLE_USER" -> "Usuario";
            default -> role;
        };
    }

    public static String translateContainerStatus(String status) {
        return switch (status) {
            case "AVAILABLE" -> "Disponible";
            case "FULL" -> "Lleno";
            case "UNDER_MAINTENANCE" -> "En mantenimiento";
            default -> status;
        };
    }


}
