package com.tesisUrbe.backend.solidWasteManagement.dto;

import com.tesisUrbe.backend.solidWasteManagement.enums.ContainerStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContainerUpdateDto {
    private String serial;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal capacity;
    private ContainerStatus status;
    private Long containerTypeId;
}
