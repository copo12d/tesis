package com.tesisUrbeTemp2.backend.solidWasteManagement.repository;

import com.tesisUrbeTemp2.backend.entities.solidWaste.Waste;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WasteRepository extends JpaRepository<Waste, Long> {

}
