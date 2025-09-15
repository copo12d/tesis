package com.tesisUrbe.backend.solidWasteManagement.dto;

import com.tesisUrbe.backend.solidWasteManagement.enums.ContainerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ContainerResponseDto {
    private Long id;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal capacity;
    private ContainerStatus status;
    private String containerTypeName;
    private LocalDateTime createdAt;
}
