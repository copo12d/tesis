package com.tesisUrbe.backend.solidWasteManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchEncResponseDto {
    private Long id;
    private String creationDate;
    private String description;
    private BigDecimal totalWeight;
    private String status;
    private String processedAt;
    private String createdByUsername;
    private String processedByUsername;
}
