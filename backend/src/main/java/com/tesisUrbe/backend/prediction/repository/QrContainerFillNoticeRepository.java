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
import com.tesisUrbe.backend.prediction.dto.QrReportProjection;
import com.tesisUrbe.backend.prediction.model.ContainerFillCycle;
import com.tesisUrbe.backend.prediction.model.QrContainerFillNotice;

public interface QrContainerFillNoticeRepository extends JpaRepository<QrContainerFillNotice, Long> {

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
                AND (:serial IS NULL OR str(qr.container.serial) LIKE str(:serial))
                AND (:reporterIp IS NULL OR str(qr.reporterIp) LIKE str(:reporterIp))
                AND qr.timeFillingNotice >= COALESCE(:startDate, qr.timeFillingNotice)
                AND qr.timeFillingNotice <= COALESCE(:endDate, qr.timeFillingNotice)
                AND (:deleted IS NULL OR qr.containerFillCycle.deleted = :deleted )
            """)
    Page<QrReportProjection> searchQrReport(
            @Param("serial") String serial,
            @Param("reporterIp") String reporterIp,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("deleted") Boolean deleted,
            Pageable pageable);

}
