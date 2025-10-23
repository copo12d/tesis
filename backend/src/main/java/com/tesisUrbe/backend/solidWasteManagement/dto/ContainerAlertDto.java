package com.tesisUrbe.backend.solidWasteManagement.dto;
import com.tesisUrbe.backend.solidWasteManagement.enums.ContainerStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;


public record ContainerAlertDto(
        Long id,
        String serial,
        BigDecimal latitude,
        BigDecimal longitude,
        String containerType,
        ContainerStatus status,
        LocalDateTime lastUpdated
) {}

