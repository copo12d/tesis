package com.tesisUrbe.backend.solidWasteManagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;

@Data
public class ContainerRequestDto {

    @NotBlank(message = "El campo serial es obligatorio")
    private String serial;

    @NotNull(message = "La latitud es obligatoria")
    @Digits(integer = 3, fraction = 6, message = "La latitud debe ser un número válido con hasta 6 decimales")
    private BigDecimal latitude;

    @NotNull(message = "La longitud es obligatoria")
    @Digits(integer = 3, fraction = 6, message = "La longitud debe ser un número válido con hasta 6 decimales")
    private BigDecimal longitude;

    @NotNull(message = "La capacidad es obligatoria")
    @DecimalMin(value = "0.1", inclusive = true, message = "La capacidad debe ser mayor a cero")
    private BigDecimal capacity;

    @NotNull(message = "El tipo de contenedor es obligatorio")
    private Long containerTypeId;
}
