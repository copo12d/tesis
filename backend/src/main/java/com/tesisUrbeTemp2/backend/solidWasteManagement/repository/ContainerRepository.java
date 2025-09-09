package com.tesisUrbeTemp2.backend.solidWasteManagement.repository;

import com.tesisUrbeTemp2.backend.entities.solidWaste.Container;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerRepository extends JpaRepository<Container, Long> {
}
