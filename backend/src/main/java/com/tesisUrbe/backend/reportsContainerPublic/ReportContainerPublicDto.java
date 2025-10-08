package com.tesisUrbe.backend.reportsContainerPublic;

import com.tesisUrbe.backend.solidWasteManagement.enums.ContainerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportContainerPublicDto {
    private Long containerId;
    private ContainerStatus previousStatus;
    private ContainerStatus newStatus;
    private Double fillLevel;
    private String message;
    private Boolean validForPrediction;
    private Boolean escalatedToCycle;
}
