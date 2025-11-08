package com.tesisUrbe.backend.solidWasteManagement.controllers;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchRegResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.services.BatchRegService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/batch-reg")
public class BatchRegController {

    private final BatchRegService batchRegService;

    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<List<BatchRegResponseDto>>> getAllBatchRegs() {
        ApiResponse<List<BatchRegResponseDto>> response = batchRegService.getAllBatchRegs();
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/admin/by-batch/{batchId}/advanced-filter")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Page<BatchRegResponseDto>>> getByBatchEncWithFilters(
            @PathVariable Long batchId,
            @RequestParam(required = false) String serial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @PageableDefault(size = 10, sort = "collectionDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        String sanitizedSerial = (serial == null || serial.isBlank()) ? null : serial.trim();

        ApiResponse<Page<BatchRegResponseDto>> response =
                batchRegService.getByBatchEncWithFilters(batchId, sanitizedSerial, start, end, pageable);

        return ResponseEntity
                .status(HttpStatus.valueOf(response.meta().status()))
                .body(response);
    }

    @GetMapping("/public/daily-summary")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDailyContainerSummary() {
        ApiResponse<List<Map<String, Object>>> response = batchRegService.getDailyContainerSummary();
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/public/weekly-summary")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getWeeklyContainerSummary() {
        ApiResponse<List<Map<String, Object>>> response = batchRegService.getWeeklyContainerSummary();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/public/monthly-summary")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMonthlyContainerSummary() {
        ApiResponse<List<Map<String, Object>>> response = batchRegService.getMontlyContainerSummary();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
