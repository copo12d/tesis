package com.tesisUrbe.backend.solidWasteManagement.repository;

import com.tesisUrbe.backend.entities.solidWaste.BatchReg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface BatchRegRepository extends JpaRepository<BatchReg, Long> {

    List<BatchReg> findByDeletedFalse();

    List<BatchReg> findByBatchEncIdAndDeletedFalse(Long batchEncId);

    @Query("""
                SELECT r FROM BatchReg r
                WHERE r.deleted = false
                  AND r.batchEnc.id = :batchEncId
                  AND (:serial IS NULL OR LOWER(r.container.serial) = LOWER(:serial))
                  AND (:start IS NULL OR r.collectionDate >= :start)
                  AND (:end IS NULL OR r.collectionDate <= :end)
            """)
    Page<BatchReg> findFiltered(
            @Param("batchEncId") Long batchEncId,
            @Param("serial") String serial,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

}
