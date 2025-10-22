package com.tesisUrbe.backend.prediction.repository;

import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.prediction.model.ContainerFillCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContainerFillCycleRepository extends JpaRepository<ContainerFillCycle, Long> {

    Optional<ContainerFillCycle> findTop1ByContainerAndDeletedFalseOrderByTimeFillingNoticeDesc(Container container);

    Optional<ContainerFillCycle> findByContainerAndDeletedFalseAndMinutesToEmptyNull(Container container);

    @Query("""
            SELECT fd
            FROM ContainerFillCycle fd
            WHERE fd.deleted = false""")
    List<ContainerFillCycle> getAllCycleData(@Param("container") Container container);

}