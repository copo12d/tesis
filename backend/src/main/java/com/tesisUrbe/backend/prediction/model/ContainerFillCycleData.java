package com.tesisUrbe.backend.prediction.model;

import com.tesisUrbe.backend.entities.solidWaste.Container;
import jakarta.persistence.*;
import lombok.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;

@Entity
@Table(name = "container_fill_cycle_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerFillCycleData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id", nullable = false)
    private Container container;

    @Column(name = "time_filling_notice", nullable = false)
    private LocalDateTime timeFillingNotice;

    @Column(name = "hours_between_filling", nullable = false)
    private Double hoursBetweenFilling;

    @Column(name = "minutes_to_empty")
    private Long minutesToEmpty = null;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Enumerated(EnumType.STRING)
    @Column(name = "month_of_year", nullable = false)
    private Month monthOfYear;

    @Column(name = "day_filling_number", nullable = false)
    private Integer dayFillingNumber;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    private String reporterIp;

    @PrePersist
    protected void onCreate() {
        LocalDateTime noticeTime = LocalDateTime.now();
        this.timeFillingNotice = noticeTime;
        this.dayOfWeek = noticeTime.getDayOfWeek();
        this.monthOfYear = noticeTime.getMonth();
    }

}