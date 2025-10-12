package com.tesisUrbe.backend.solidWasteManagement.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BatchStatus {
    IN_PROGRESS("Lote en proceso"),
    PROCESSED("Lote Procesado");

    private final String description;
}
