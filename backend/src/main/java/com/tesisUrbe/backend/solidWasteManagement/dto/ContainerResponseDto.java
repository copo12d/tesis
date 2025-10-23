package com.tesisUrbe.backend.solidWasteManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor 
public class ContainerResponseDto {
    private Long id;
    private String serial;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal capacity;
    private String status;
    private String containerTypeName;
    private LocalDateTime createdAt;
}
