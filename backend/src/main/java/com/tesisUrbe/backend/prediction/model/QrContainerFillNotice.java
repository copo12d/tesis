package com.tesisUrbe.backend.prediction.model;

import java.time.LocalDateTime;

import com.tesisUrbe.backend.entities.solidWaste.Container;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "qr_container_fill_notices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrContainerFillNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id", nullable = false)
    private Container container;

    @Column(name = "reporter_ip", nullable = false)
    private String reporterIp;

    @Builder.Default
    @Column(name = "time_filling_notice", nullable = false)
    private LocalDateTime timeFillingNotice = LocalDateTime.now();

    @Builder.Default
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_fill_cycle_id", nullable = true) 
    private ContainerFillCycle containerFillCycle = null; 
}