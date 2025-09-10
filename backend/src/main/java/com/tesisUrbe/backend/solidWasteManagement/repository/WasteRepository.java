package com.tesisUrbe.backend.solidWasteManagement.repository;

import com.tesisUrbe.backend.entities.solidWaste.Waste;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WasteRepository extends JpaRepository<Waste, Long> {

}
