package com.tesisUrbe.backend.solidWasteManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class WasteResponseDto {
    private Long id;
    private BigDecimal weight;
    private LocalDate collectionDate;
    private Long containerId;
    private Long batchId;
}
