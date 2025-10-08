package com.tesisUrbe.backend.reportsContainerPublic;

import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.solidWasteManagement.enums.ContainerStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "report_container_public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportContainerPublic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "container_id", nullable = false)
    private Container container;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private ContainerStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status")
    private ContainerStatus newStatus;

    @Column(name = "fill_level")
    private Double fillLevel;

    @Column(name = "message", length = 1000)
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "notified", nullable = false)
    private boolean notified = false;

    @Column(name = "valid_for_prediction", nullable = false)
    private boolean validForPrediction = false;

    @Column(name = "escalated_to_cycle", nullable = false)
    private boolean escalatedToCycle = false;

    @Column(name = "evaluated_at")
    private LocalDateTime evaluatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
