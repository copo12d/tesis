package com.tesisUrbe.backend.common.util;

public class PageValidator {

    public static void validate(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("El número de página no puede ser negativo");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("El tamaño de página debe estar entre 1 y 100");
        }
    }
}