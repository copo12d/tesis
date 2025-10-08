package com.tesisUrbe.backend.reportsContainerPublic;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportContainerPublicRepository extends JpaRepository<ReportContainerPublic, Long> {
    List<ReportContainerPublic> findByNotifiedFalse();
}
