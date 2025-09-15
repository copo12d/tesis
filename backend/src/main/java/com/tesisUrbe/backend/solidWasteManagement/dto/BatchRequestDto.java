package com.tesisUrbe.backend.solidWasteManagement.dto;

import com.tesisUrbe.backend.solidWasteManagement.enums.BatchStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BatchRequestDto {

    @NotNull
    @PastOrPresent
    private LocalDate creationDate;

    private String description;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal totalWeight;

    @NotNull
    private BatchStatus status;

    private LocalDate shippingDate;
}
