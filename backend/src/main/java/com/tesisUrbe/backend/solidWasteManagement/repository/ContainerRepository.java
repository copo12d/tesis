package com.tesisUrbe.backend.solidWasteManagement.repository;

import com.tesisUrbe.backend.entities.solidWaste.Container;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ContainerRepository extends JpaRepository<Container, Long> {

    boolean existsBySerial(String serial);

    @Query("""
        SELECT c FROM Container c
        WHERE c.deleted = false
    """)
    Page<Container> findByDeletedFalse(Pageable pageable);

    @Query("""
        SELECT c FROM Container c
        WHERE c.deleted = false AND c.id = :id
    """)
    Page<Container> findByIdAndDeletedFalse(@Param("id") Long id, Pageable pageable);

    @Query("""
        SELECT c FROM Container c
        WHERE c.deleted = false AND LOWER(c.serial) LIKE LOWER(CONCAT('%', :serial, '%'))
    """)
    Page<Container> findBySerialContainingIgnoreCaseAndDeletedFalse(@Param("serial") String serial, Pageable pageable);
}
