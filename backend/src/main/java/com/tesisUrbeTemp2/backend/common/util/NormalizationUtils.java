package com.tesisUrbeTemp2.backend.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
}
