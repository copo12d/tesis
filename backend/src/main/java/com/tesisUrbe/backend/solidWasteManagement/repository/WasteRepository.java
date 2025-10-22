package com.tesisUrbe.backend.solidWasteManagement.repository;

import com.tesisUrbe.backend.entities.solidWaste.Waste;
import com.tesisUrbe.backend.solidWasteManagement.dto.WasteWeightProyection;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WasteRepository extends JpaRepository<Waste, Long> {

    Page<Waste> findByDeletedFalse(Pageable pageable);

    Page<Waste> findByDeletedFalseAndContainer_SerialContainingIgnoreCase(String serial, Pageable pageable);


    @Query("""    
            SELECT 
                w.container.containerType.name AS containerType,
                DAY_OF_WEEK(w.collectionDate) AS collectionDayOfWeek,
                YEAR(w.collectionDate) AS collectionYear,
                MONTH(w.collectionDate) AS collectionMonth,
                SUM(w.weight) AS totalWeight
            FROM Waste w
            WHERE w.deleted = false
            GROUP BY w.container.containerType.name, DAY_OF_WEEK(w.collectionDate), YEAR(w.collectionDate), MONTH(w.collectionDate)
            """)
    List<WasteWeightProyection> getAllWeightTotal();


}
