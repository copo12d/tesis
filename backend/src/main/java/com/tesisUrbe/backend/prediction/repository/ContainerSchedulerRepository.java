package com.tesisUrbe.backend.prediction.repository;

import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.prediction.dto.SchedulerProjection;
import com.tesisUrbe.backend.prediction.model.ContainerScheduler;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ContainerSchedulerRepository extends JpaRepository<ContainerScheduler, Long> {

    @Query("""
            SELECT cs
            FROM ContainerScheduler cs
            WHERE
                cs.container = :container AND
                cs.wasUsed = FALSE AND
                cs.wasSuspended = FALSE
            ORDER BY cs.schedulerFillTime ASC
            FETCH FIRST 1 ROWS ONLY""")
    Optional<ContainerScheduler> findNextRecollectionByContainer(@Param("container") Container container);

    @Modifying
    @Query("""
                UPDATE ContainerScheduler cs
                SET cs.wasSuspended = TRUE
                WHERE cs.container = :container AND
                  cs.schedulerFillTime > :currentTime AND
                  cs.schedulerFillTime < :cutOffTime AND
                  cs.wasSuspended = FALSE
            """)
    void autoSuspendSchedulesByContainer(
            @Param("container") Container container,
            @Param("currentTime") LocalDateTime currentTime,
            @Param("cutOffTime") LocalDateTime cutOffTime
    );

    @Modifying
    @Query("""
            UPDATE ContainerScheduler cs
            SET cs.wasSuspended = FALSE
            WHERE cs.container = :container AND
              cs.schedulerFillTime > :currentTime AND
              cs.schedulerFillTime < :cutOffTime AND
              cs.wasSuspended = TRUE
            """)
    void autoUnsuspendSchedulesByContainer(
            @Param("container") Container container,
            @Param("currentTime") LocalDateTime currentTime,
            @Param("cutOffTime") LocalDateTime cutOffTime
    );

    @Query("""
            SELECT
                cs.container as container,
                cs.schedulerFillTime as schedulerFillTime
            FROM ContainerScheduler cs
            WHERE
              cs.schedulerFillTime <= :referenceTime AND
              cs.wasUsed = FALSE AND
              cs.wasSuspended = FALSE
            """)
    List<SchedulerProjection> findAllActiveScheduler(@Param("referenceTime") LocalDateTime referenceTime);

    @Modifying
    @Query("""
            UPDATE ContainerScheduler cs
            SET cs.wasUsed = TRUE
            WHERE cs.container = :container AND
              cs.schedulerFillTime <= :referenceTime AND
              cs.wasUsed = FALSE
            """)
    void setAllUsed(
            @Param("container") Container container,
            @Param("referenceTime") LocalDateTime referenceTime);

    @Modifying
    @Query("""
            UPDATE ContainerScheduler cs
            SET cs.wasSuspended = TRUE
            WHERE cs.container = :container AND
              cs.schedulerFillTime <= :referenceTime AND
              cs.wasSuspended = FALSE
            """)
    void suspendForCurrentCycle(@Param("container") Container container, @Param("referenceTime") LocalDateTime referenceTime);

}
