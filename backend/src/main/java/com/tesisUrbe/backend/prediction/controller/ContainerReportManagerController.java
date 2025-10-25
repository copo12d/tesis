package com.tesisUrbe.backend.prediction.controller;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.prediction.dto.NextRecollectionDto;
import com.tesisUrbe.backend.prediction.dto.QrReportResponseDto;
import com.tesisUrbe.backend.prediction.service.ContainerFillCycleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/container")
public class ContainerReportManagerController {

    private final ContainerFillCycleService containerFillCycleService;

    
    @PostMapping("/public/report")
    public ResponseEntity<ApiResponse<Void>> reportContainerFull(@RequestParam String serial, HttpServletRequest request) {
        ApiResponse<Void> response = containerFillCycleService.reportContainerBySerial(serial, request);

        return ResponseEntity.status(response.meta().status()).body(response);
    }

    
    @PostMapping("/employee/recollect-cancel")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<ApiResponse<Void>> cancelContainerFillCycle(
            @RequestParam String serial,
            @RequestBody Map<String, String> reason) {

        ApiResponse<Void> response = containerFillCycleService.cancelContainerNotice(
                serial,
                reason.getOrDefault("reason", null));

        return ResponseEntity.status(response.meta().status()).body(response);
    }

    @GetMapping("/admin/qr-reports")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Page<QrReportResponseDto>>> searchQrReports(
            @RequestParam(required = false) String serial,
            @RequestParam(required = false) String reporterIp,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Boolean deleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "reportTime") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        ApiResponse<Page<QrReportResponseDto>> response = containerFillCycleService.searchQrReport(
                pageable, serial, reporterIp, startDate, endDate, deleted);

        return ResponseEntity.status(response.meta().status()).body(response);
    }

    @GetMapping("/public/next-recollection/{serial}")
    public ResponseEntity<ApiResponse<NextRecollectionDto>> publicNextRecollection(@PathVariable("serial") String serial) {
        ApiResponse<NextRecollectionDto> response = containerFillCycleService.nextRecollectionBySerial(serial);

        return ResponseEntity.status(response.meta().status()).body(response);
    }

}
