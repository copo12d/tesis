package com.tesisUrbe.backend.prediction.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.prediction.model.ContainerFillCycle;
import com.tesisUrbe.backend.prediction.model.QrContainerFillNotice;

public interface QrContainerFillNoticeRepository extends JpaRepository<QrContainerFillNotice, Long>{

    boolean existsByContainerAndReporterIpAndTimeFillingNoticeAfter(Container container, String ip, LocalDateTime after);

    long countDistinctReporterIpByContainerAndContainerFillCycleNull(Container container);

    @Modifying
    @Query("UPDATE QrContainerFillNotice q SET q.containerFillCycle = :cycle WHERE q.container = :container AND q.containerFillCycle IS NULL")
    void linkNoticesToCycle(@Param("cycle") ContainerFillCycle cycle, @Param("container") Container container);
    
}
