package com.tesisUrbe.backend.solidWasteManagement.repository;

import com.tesisUrbe.backend.entities.solidWaste.ContainerType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerTypeRepository extends JpaRepository<ContainerType, Long> {
}
