package com.tesisUrbe.backend.prediction.repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.prediction.dto.QrReportProyection;
import com.tesisUrbe.backend.prediction.model.ContainerFillCycle;
import com.tesisUrbe.backend.prediction.model.QrContainerFillNotice;

public interface QrContainerFillNoticeRepository extends JpaRepository<QrContainerFillNotice, Long>{

    boolean existsByContainerAndReporterIpAndTimeFillingNoticeAfter(Container container, String ip, LocalDateTime after);

    long countDistinctReporterIpByContainerAndContainerFillCycleNull(Container container);

    @Modifying
    @Query("UPDATE QrContainerFillNotice q SET q.containerFillCycle = :cycle WHERE q.container = :container AND q.containerFillCycle IS NULL")
    void linkNoticesToCycle(@Param("cycle") ContainerFillCycle cycle, @Param("container") Container container);

    @Query("""
        SELECT
            qr.container.serial as containerSerial,
            qr.reporterIp as reporterIp,
            qr.timeFillingNotice as reportTime,
            qr.containerFillCycle.deleted as deleteCycle
        FROM QrContainerFillNotice qr
        WHERE qr.containerFillCycle IS NOT NULL
        AND (:serial IS NULL OR LOWER(qr.container.serial) LIKE LOWER(CONCAT('%', :serial, '%')))
        AND (:reporterIp IS NULL OR qr.reporterIp = :reporterIp)
        AND (:startDate IS NULL OR qr.timeFillingNotice >= :startDate)
        AND (:endDate IS NULL OR qr.timeFillingNotice <= :endDate)
        AND (:dayOfWeek IS NULL OR qr.containerFillCycle.dayOfWeek = :dayOfWeek)
        AND (:month IS NULL OR qr.containerFillCycle.monthOfYear = :month )
        AND (:deleted IS NULL OR qr.containerFillCycle.deleted = :deleted )

            """)
    Page<QrReportProyection> searchQrReport(
        @Param("serial") String serial,
        @Param("reporterIp") String reporterIp,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("dayOfWeek") DayOfWeek dayOfWeek,
        @Param("month") Month month,
        @Param("deleted") Boolean deleted,
        Pageable pageable);
    
}
