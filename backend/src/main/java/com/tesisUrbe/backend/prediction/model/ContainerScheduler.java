package com.tesisUrbe.backend.prediction.model;

import com.tesisUrbe.backend.entities.solidWaste.Container;
import jakarta.persistence.*;
import lombok.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;

@Entity
@Table(name = "container_scheduler")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerScheduler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id", nullable = false)
    private Container container;

    @Column(name = "scheduler_fill_time", nullable = false)
    private LocalDateTime schedulerFillTime;

    @Column(name = "was_used", nullable = false)
    private  boolean wasUsed;

    @Column(name = "was_suspended", nullable = false)
    private boolean wasSuspended = false;
}