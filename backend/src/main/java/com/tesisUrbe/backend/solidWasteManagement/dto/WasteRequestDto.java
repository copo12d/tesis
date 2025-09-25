package com.tesisUrbe.backend.solidWasteManagement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class WasteRequestDto {

    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal weight;

    @NotNull
    private LocalDate collectionDate;

    @NotNull
    private Long containerId;

    @NotNull
    private Long batchId;
}
