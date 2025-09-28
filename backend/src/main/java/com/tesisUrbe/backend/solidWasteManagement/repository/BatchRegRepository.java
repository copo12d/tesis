package com.tesisUrbe.backend.solidWasteManagement.repository;

import com.tesisUrbe.backend.entities.solidWaste.BatchReg;
import com.tesisUrbe.backend.entities.solidWaste.Waste;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRegRepository extends JpaRepository<BatchReg, Long> {

    List<BatchReg> findByDeletedFalse();

    List<BatchReg> findByBatchEncIdAndDeletedFalse(Long batchEncId);

}
