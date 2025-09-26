package com.tesisUrbe.backend.solidWasteManagement.repository;

import com.tesisUrbe.backend.entities.solidWaste.Waste;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WasteRepository extends JpaRepository<Waste, Long> {

    Page<Waste> findByDeletedFalse(Pageable pageable);

    Page<Waste> findByDeletedFalseAndContainer_SerialContainingIgnoreCase(String serial, Pageable pageable);
}
