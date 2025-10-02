package com.tesisUrbe.backend.solidWasteManagement.repository;

import com.tesisUrbe.backend.entities.solidWaste.BatchEnc;
import com.tesisUrbe.backend.solidWasteManagement.enums.BatchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BatchEncRepository extends JpaRepository<BatchEnc, Long> {

    boolean existsByIdAndDeletedFalse(Long id);

    Page<BatchEnc> findByDeletedFalse(Pageable pageable);

    Optional<BatchEnc> findByIdAndDeletedFalse(Long id);

    @Query("SELECT b FROM BatchEnc b " +
            "WHERE b.deleted = false " +
            "AND (:description IS NULL OR :description = '' OR LOWER(b.description) LIKE LOWER(CONCAT('%', :description, '%'))) " +
            "AND (:status IS NULL OR b.status = :status) " +
            "AND (:fechaInicio IS NULL OR b.creationDate >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR b.creationDate <= :fechaFin)")
    Page<BatchEnc> findByAdvancedSearch(
            @Param("description") String description,
            @Param("status") BatchStatus status,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            Pageable pageable
    );

    @Query("SELECT b FROM BatchEnc b " +
            "WHERE b.deleted = false " +
            "AND (:fechaInicio IS NULL OR b.creationDate >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR b.creationDate <= :fechaFin)")
    List<BatchEnc> findAllForReportByDateRange(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );
    List<BatchEnc> findByCreationDateBetweenAndDeletedFalse(LocalDate start, LocalDate end);
    List<BatchEnc> findByCreationDateGreaterThanEqualAndDeletedFalse(LocalDate start);
    List<BatchEnc> findByCreationDateLessThanEqualAndDeletedFalse(LocalDate end);
    List<BatchEnc> findByDeletedFalse();

}
