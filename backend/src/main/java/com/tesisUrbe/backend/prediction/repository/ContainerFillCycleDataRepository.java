package com.tesisUrbe.backend.prediction.repository;

import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.prediction.dto.ContainerRecollectTimeData;
import com.tesisUrbe.backend.prediction.model.ContainerFillCycleData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

public interface ContainerFillCycleDataRepository extends JpaRepository<ContainerFillCycleData, Long> {

    Optional<ContainerFillCycleData> findTop1ByContainerAndDeletedFalseOrderByTimeFillingNoticeDesc(Container container);

    @Query("""
    SELECT fd FROM ContainerFillCycleData fd
    WHERE 
        fd.container = :container AND
        fd.dayOfWeek = :dayOfWeek AND
        fd.monthOfYear = :monthOfYear AND
        fd.deleted = false""")
    List<ContainerFillCycleData> getAllFillCycleData(
            @Param("container") Container container,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("monthOfYear") Month monthOfYear);

    boolean existsByContainerAndReporterIpAndTimeFillingNoticeAfter(Container container, String ip, LocalDateTime after);

    long countDistinctReporterIpByContainerAndTimeFillingNoticeAfter(Container container, LocalDateTime after);

    @Query("""
        SELECT 
            fd.container as container,
            fd.dayOfWeek as dayOfWeek,
            fd.monthOfYear as month, 
            AVG(fd.minutesToEmpty) as averageTime
        FROM ContainerFillCycleData fd
        WHERE fd.deleted = false
            """)
    List<ContainerRecollectTimeData> getAllRecollectTimeDatas();
    

}