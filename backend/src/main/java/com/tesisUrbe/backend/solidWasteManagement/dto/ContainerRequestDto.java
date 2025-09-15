package com.tesisUrbe.backend.solidWasteManagement.dto;

import com.tesisUrbe.backend.solidWasteManagement.enums.ContainerStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContainerRequestDto {
    @NotNull
    private BigDecimal latitude;

    @NotNull
    private BigDecimal longitude;

    @NotNull
    private BigDecimal capacity;

    @NotNull
    private ContainerStatus status;

    @NotNull
    private Long containerTypeId;
}
