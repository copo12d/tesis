package com.tesisUrbe.backend.solidWasteManagement.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContainerStatus {
    AVAILABLE("Contenedor Vacio"),
    FULL("Contendedor Lleno"),
    UNDER_MAINTENANCE("En mantenimiento");

    private final String description;
}
