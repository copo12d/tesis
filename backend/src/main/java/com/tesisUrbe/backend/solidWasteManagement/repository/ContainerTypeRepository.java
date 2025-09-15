package com.tesisUrbe.backend.solidWasteManagement.repository;

import com.tesisUrbe.backend.entities.solidWaste.ContainerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerTypeRepository extends JpaRepository<ContainerType, Long> {
    boolean existsByName(String name);
    Page<ContainerType> findByDeletedFalseAndNameContainingIgnoreCase(String name, Pageable pageable);
    Page<ContainerType> findByDeletedFalse(Pageable pageable);
}
