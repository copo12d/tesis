package com.tesisUrbe.backend.solidWasteManagement.controllers;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.common.util.PageValidator;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchRegResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.services.BatchRegService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/admin/by-batch/{batchId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<List<BatchRegResponseDto>>> getDetailsForBatch(@PathVariable Long batchId) {
        ApiResponse<List<BatchRegResponseDto>> response = batchRegService.getDetailsForBatch(batchId);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/admin/search")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Page<BatchRegResponseDto>>> searchBatchRegsAdvanced(
            @RequestParam(required = false) Long containerId,
            @RequestParam(required = false) Long batchEncId,
            @RequestParam(required = false) String createdByUsername,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "collectionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        PageValidator.validate(page, size);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        ApiResponse<Page<BatchRegResponseDto>> response = batchRegService.searchAdvanced(
                containerId, batchEncId, createdByUsername, fechaInicio, fechaFin, pageable
        );
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }
    
}
