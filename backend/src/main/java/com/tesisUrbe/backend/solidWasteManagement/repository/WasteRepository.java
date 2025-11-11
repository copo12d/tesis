package com.tesisUrbe.backend.solidWasteManagement.repository;

import com.tesisUrbe.backend.entities.solidWaste.Waste;
import com.tesisUrbe.backend.solidWasteManagement.dto.WasteWeightProyection;

import java.util.List;

import com.tesisUrbe.backend.solidWasteManagement.dto.WasteWeightProyectionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WasteRepository extends JpaRepository<Waste, Long> {

    Page<Waste> findByDeletedFalse(Pageable pageable);

    Page<Waste> findByDeletedFalseAndContainer_SerialContainingIgnoreCase(String serial, Pageable pageable);


    @Query(value = """
    SELECT 
        ct.name AS containerType,
        EXTRACT(DOW FROM w.collection_date) AS collectionDayOfWeek,
        EXTRACT(YEAR FROM w.collection_date) AS collectionYear,
        EXTRACT(MONTH FROM w.collection_date) AS collectionMonth,
        SUM(w.weight) AS totalWeight
    FROM waste w
    JOIN container c ON c.id = w.container_id
    JOIN container_type ct ON ct.id = c.container_type_id
    WHERE w.deleted = false
    GROUP BY ct.name, EXTRACT(DOW FROM w.collection_date), EXTRACT(YEAR FROM w.collection_date), EXTRACT(MONTH FROM w.collection_date)
    """, nativeQuery = true)
    List<WasteWeightProyectionDTO> getAllWeightTotal();

}
