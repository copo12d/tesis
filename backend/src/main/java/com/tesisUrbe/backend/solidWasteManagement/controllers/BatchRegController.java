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


}
