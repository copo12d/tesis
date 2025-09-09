package com.tesisUrbeTemp2.backend.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationUtils {

    public static void validateRequiredFields(Object dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El objeto no puede ser nulo");
        }

        try {
            for (Field field : dto.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(dto);

                if (value == null) {
                    throw new IllegalArgumentException("El campo '" + field.getName() + "' es obligatorio");
                }

                if (value instanceof String && ((String) value).trim().isEmpty()) {
                    throw new IllegalArgumentException("El campo '" + field.getName() + "' no puede estar vacío");
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error al acceder a los campos del objeto", e);
        }
    }
}
