package com.tesisUrbe.backend.solidWasteManagement.repository;

import com.tesisUrbe.backend.entities.solidWaste.Container;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerRepository extends JpaRepository<Container, Long> {
    Page<Container> findByDeletedFalse(Pageable pageable);
    Page<Container> findByDeletedFalseAndContainerType_NameContainingIgnoreCase(String name, Pageable pageable);
}
