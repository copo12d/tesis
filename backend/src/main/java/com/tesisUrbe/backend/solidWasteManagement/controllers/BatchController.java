package com.tesisUrbe.backend.solidWasteManagement.controllers;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchRequestDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.services.BatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/batch")
public class BatchController {

    private final BatchService batchService;

    @PostMapping("/admin/register")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Void>> registerBatch(@Valid @RequestBody BatchRequestDto dto) {
        ApiResponse<Void> response = batchService.registerBatch(dto);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<BatchResponseDto>> getBatchById(@PathVariable Long id) {
        ApiResponse<BatchResponseDto> response = batchService.getBatchById(id);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Page<BatchResponseDto>>> getAllBatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        ApiResponse<Page<BatchResponseDto>> response = batchService.getAllBatches(page, size, sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }
}
