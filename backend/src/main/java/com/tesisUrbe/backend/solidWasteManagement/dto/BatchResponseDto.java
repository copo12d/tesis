package com.tesisUrbe.backend.solidWasteManagement.dto;

import com.tesisUrbe.backend.solidWasteManagement.enums.BatchStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BatchResponseDto {
    private Long id;
    private LocalDate creationDate;
    private String description;
    private BigDecimal totalWeight;
    private BatchStatus status;
    private LocalDate shippingDate;
}
