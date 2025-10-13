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
public class BatchRegResponseDto {
    private String serial;
    private BigDecimal weight;
    private String createdByUsername;
    private String date;
    private String hour;
}
