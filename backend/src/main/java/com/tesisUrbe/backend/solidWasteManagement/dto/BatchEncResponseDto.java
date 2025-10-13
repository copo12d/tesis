package com.tesisUrbe.backend.solidWasteManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchEncResponseDto {
    private Long id;
    private LocalDateTime creationDate;
    private String description;
    private BigDecimal totalWeight;
    private String status;
    private LocalDateTime processedAt;
    private String createdByUsername;
    private String processedByUsername;
}
