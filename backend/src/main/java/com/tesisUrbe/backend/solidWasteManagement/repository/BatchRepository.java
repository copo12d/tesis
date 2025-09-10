package com.tesisUrbe.backend.solidWasteManagement.repository;

import com.tesisUrbe.backend.entities.solidWaste.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<Batch, Long> {

}
