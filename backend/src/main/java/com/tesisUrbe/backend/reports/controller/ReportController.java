package com.tesisUrbe.backend.reports.controller;

import com.tesisUrbe.backend.reports.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("admin/batch/{batchId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<byte[]> generateBatchReportPdf(@PathVariable Long batchId) {
        return reportService.generateBatchReportPdf(batchId);
    }

    @GetMapping("admin/batch/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<byte[]> generateBatchEncReport(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin
    ) {
        return reportService.generateBatchEncReport(fechaInicio, fechaFin);
    }

    @GetMapping("admin/users/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<byte[]> generateAllUsersPdf(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String verified,
            @RequestParam(required = false) String accountLocked,
            @RequestParam(required = false) String userLocked,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        return reportService.generateAllUsersPdf(role, verified, accountLocked, userLocked, sortBy, sortDir);
    }

}
