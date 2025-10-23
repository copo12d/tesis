package com.tesisUrbe.backend.prediction.repository;

import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.prediction.dto.ContainerRecollectTimeProyection;
import com.tesisUrbe.backend.prediction.model.ContainerFillCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.List;
import java.util.Optional;

public interface ContainerFillCycleRepository extends JpaRepository<ContainerFillCycle, Long> {

    Optional<ContainerFillCycle> findTop1ByContainerAndDeletedFalseOrderByTimeFillingNoticeDesc(Container container);

    Optional<ContainerFillCycle> findByContainerAndDeletedFalseAndMinutesToEmptyNull(Container container);

    @Query("""
    SELECT fd FROM ContainerFillCycle fd
    WHERE 
        fd.container = :container AND
        fd.dayOfWeek = :dayOfWeek AND
        fd.monthOfYear = :monthOfYear AND
        fd.deleted = false""")
    List<ContainerFillCycle> getAllFillCycleData(
            @Param("container") Container container,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("monthOfYear") Month monthOfYear);

    @Query("""
        SELECT 
            fd.container as container,
            fd.dayOfWeek as dayOfWeek,
            fd.monthOfYear as month, 
            AVG(fd.minutesToEmpty) as averageTime
        FROM ContainerFillCycle fd
        WHERE fd.deleted = false
            """)
    List<ContainerRecollectTimeProyection> getAllRecollectTimeDatas();
    

}