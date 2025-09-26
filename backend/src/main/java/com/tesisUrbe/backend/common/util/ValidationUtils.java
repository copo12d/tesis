package com.tesisUrbe.backend.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

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

    public static <E extends Enum<E>> String buildValidEnumMessage(Class<E> enumClass, String label) {
        String validValues = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        return label + " inválido. Valores permitidos: " + validValues;
    }

}
