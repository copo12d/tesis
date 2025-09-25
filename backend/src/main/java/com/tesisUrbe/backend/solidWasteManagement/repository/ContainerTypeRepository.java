package com.tesisUrbe.backend.solidWasteManagement.repository;

import com.tesisUrbe.backend.entities.solidWaste.ContainerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ContainerTypeRepository extends JpaRepository<ContainerType, Long> {

    boolean existsByName(String name);

    @Query("""
        SELECT ct FROM ContainerType ct
        WHERE ct.deleted = false AND (
            LOWER(ct.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
            LOWER(ct.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        )
    """)
    Page<ContainerType> searchContainerTypes(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("""
        SELECT ct FROM ContainerType ct
        WHERE ct.deleted = false
    """)
    Page<ContainerType> findAllActive(Pageable pageable);
}
