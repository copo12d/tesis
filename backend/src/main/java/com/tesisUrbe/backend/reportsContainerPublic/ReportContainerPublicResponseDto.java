package com.tesisUrbe.backend.reportsContainerPublic;

import com.tesisUrbe.backend.solidWasteManagement.enums.ContainerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportContainerPublicResponseDto {
    private Long id;
    private Long containerId;
    private ContainerStatus previousStatus;
    private ContainerStatus newStatus;
    private Double fillLevel;
    private String message;
    private LocalDateTime createdAt;
    private boolean notified;
}
