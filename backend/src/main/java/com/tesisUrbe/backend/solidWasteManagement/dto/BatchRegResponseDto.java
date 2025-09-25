package com.tesisUrbe.backend.solidWasteManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchRegResponseDto {

    private Long id;
    private LocalDate collectionDate;
    private BigDecimal weight;

    private Long containerId;

    private Long batchEncId;
    private String createdByUsername;
}
