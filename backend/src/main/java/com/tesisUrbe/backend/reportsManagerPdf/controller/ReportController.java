package com.tesisUrbe.backend.reportsManagerPdf.controller;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.reportsManagerPdf.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("admin/batch/{batchId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<?> downloadBatchReportPdf(@PathVariable Long batchId) {
        ApiResponse<byte[]> response = reportService.generateBatchReportPdf(batchId);

        if (response.data() == null || response.data().length == 0) {
            return ResponseEntity
                    .status(HttpStatus.valueOf(response.meta().status()))
                    .body(response);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_lote_" + batchId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(response.data());
    }

    @GetMapping("admin/batch/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<?> getBatchEncReport(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin
    ) {
        ApiResponse<byte[]> response = reportService.generateBatchEncReport(fechaInicio, fechaFin);

        if (response.data() == null || response.data().length == 0) {
            return ResponseEntity
                    .status(HttpStatus.valueOf(response.meta().status()))
                    .body(response);
        }

        String filename = "reporte_lotes_" +
                (StringUtils.hasText(fechaInicio) ? fechaInicio : "inicio") + "_" +
                (StringUtils.hasText(fechaFin) ? fechaFin : "fin") + ".pdf";

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(response.data());
    }

    @GetMapping("admin/users/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<?> getAllUsersPdf(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String verified,
            @RequestParam(required = false) String accountLocked,
            @RequestParam(required = false) String userLocked,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir
    ) {
        ApiResponse<byte[]> response = reportService.generateAllUsersPdf(
                role, verified, accountLocked, userLocked, sortBy, sortDir
        );

        if (response.data() == null || response.data().length == 0) {
            return ResponseEntity
                    .status(HttpStatus.valueOf(response.meta().status()))
                    .body(response);
        }

        String filename = "reporte_usuarios.pdf";

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(response.data());
    }

    @GetMapping("admin/containers/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<?> getContainerReport(
            @RequestParam(required = false) String serial,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir
    ) {
        ApiResponse<byte[]> response = reportService.generateContainerReport(serial, id, sortBy, sortDir);

        if (response.data() == null || response.data().length == 0) {
            return ResponseEntity
                    .status(HttpStatus.valueOf(response.meta().status()))
                    .body(response);
        }

        String filename = "reporte_contenedores.pdf";

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(response.data());
    }

}
