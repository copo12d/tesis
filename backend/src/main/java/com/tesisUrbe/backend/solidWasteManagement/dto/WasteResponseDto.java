package com.tesisUrbe.backend.solidWasteManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class WasteResponseDto {
    private Long id;
    private BigDecimal weight;
    private LocalDateTime collectionDate;
    private Long containerId;
    private Long batchId;
}
