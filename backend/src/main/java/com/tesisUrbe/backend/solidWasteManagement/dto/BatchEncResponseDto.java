package com.tesisUrbe.backend.solidWasteManagement.dto;

import com.tesisUrbe.backend.solidWasteManagement.enums.BatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchEncResponseDto {
    private Long id;
    private LocalDateTime creationDate;
    private String description;
    private BigDecimal totalWeight;
    private BatchStatus status;
    private LocalDateTime processedAt;
    private String createdByUsername;
    private String processedByUsername;
}
