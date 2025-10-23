package com.tesisUrbe.backend.solidWasteManagement.repository;

import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.solidWasteManagement.enums.ContainerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    Optional<Container> findBySerialAndDeletedFalse(String serial);

    @Query("""
    SELECT c FROM Container c
    WHERE c.deleted = false
""")
    List<Container> findAllByDeletedFalse();

    @Query("""
    SELECT c FROM Container c
    WHERE c.deleted = false AND c.id = :id
""")
    List<Container> findAllByIdAndDeletedFalse(@Param("id") Long id);

    @Query("""
    SELECT c FROM Container c
    WHERE c.deleted = false AND LOWER(c.serial) LIKE LOWER(CONCAT('%', :serial, '%'))
""")
    List<Container> findAllBySerialContainingIgnoreCaseAndDeletedFalse(@Param("serial") String serial);

    long countByDeletedFalse();

    List<Container> findByStatusAndDeletedFalse(ContainerStatus status);

    Long countByStatusAndDeletedFalse(ContainerStatus status);

}
