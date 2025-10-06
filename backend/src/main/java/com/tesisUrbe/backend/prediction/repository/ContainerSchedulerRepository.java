package com.tesisUrbe.backend.prediction.repository;

import com.tesisUrbe.backend.prediction.model.ContainerScheduler;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ContainerSchedulerRepository extends JpaRepository<ContainerScheduler, Long> {

    List<ContainerScheduler> findAllByWasUsedFalseAndWasSuspendedFalseAndSchedulerFillTimeBefore(
            LocalDateTime referenceTime);
}
