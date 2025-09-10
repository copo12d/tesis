package com.tesisUrbe.backend.solidWasteManagement.repository;

import com.tesisUrbe.backend.entities.solidWaste.Container;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerRepository extends JpaRepository<Container, Long> {
}
