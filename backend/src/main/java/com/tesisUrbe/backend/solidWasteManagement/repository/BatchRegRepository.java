package com.tesisUrbe.backend.solidWasteManagement.repository;

import com.tesisUrbe.backend.entities.solidWaste.BatchReg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRegRepository extends JpaRepository<BatchReg, Long> {
    List<BatchReg> findByBatchEncIdAndDeletedFalse(Long batchEncId);
    Optional<BatchReg> findByIdAndDeletedFalse(Long id);

    @Query("""
    SELECT r FROM BatchReg r
    WHERE r.deleted = false
    AND (:containerId IS NULL OR r.container.id = :containerId)
    AND (:batchEncId IS NULL OR r.batchEnc.id = :batchEncId)
    AND (:createdByUsername IS NULL OR LOWER(r.createdBy.userName) LIKE LOWER(CONCAT('%', :createdByUsername, '%')))
    AND (:fechaInicio IS NULL OR r.collectionDate >= :fechaInicio)
    AND (:fechaFin IS NULL OR r.collectionDate <= :fechaFin)
""")
    Page<BatchReg> searchAdvanced(
            @Param("containerId") Long containerId,
            @Param("batchEncId") Long batchEncId,
            @Param("createdByUsername") String createdByUsername,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            Pageable pageable
    );

}
