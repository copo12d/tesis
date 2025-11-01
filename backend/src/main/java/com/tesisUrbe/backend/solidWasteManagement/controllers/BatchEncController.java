package com.tesisUrbe.backend.solidWasteManagement.controllers;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.common.util.PageValidator;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchCountDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchDropdownDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchEncRequestDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchEncResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.services.BatchEncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/batch")
public class BatchEncController {

    private final BatchEncService batchEncService;

    @PostMapping("/admin/register")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Void>> registerBatch(@Valid @RequestBody BatchEncRequestDto dto) {
        ApiResponse<Void> response = batchEncService.registerBatch(dto);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @PatchMapping("/admin/process/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Void>> processBatch(@PathVariable Long id) {
        ApiResponse<Void> response = batchEncService.processBatch(id);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<BatchEncResponseDto>> getBatchById(@PathVariable Long id) {
        ApiResponse<BatchEncResponseDto> response = batchEncService.getBatchById(id);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/public/processed-summary")
    public ResponseEntity<ApiResponse<BatchCountDto>> getProcessedBatchCount() {
        ApiResponse<BatchCountDto> response = batchEncService.getProcessedBatchCount();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Page<BatchEncResponseDto>>> getAllBatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "creationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        PageValidator.validate(page, size);
        ApiResponse<Page<BatchEncResponseDto>> response = batchEncService.getAllBatches(page, size, sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/admin/dropdown/in-progress")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER','ROLE_EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<BatchDropdownDto>>> getInProgressBatchDropdown() {
        ApiResponse<List<BatchDropdownDto>> response = batchEncService.getInProgressBatchDropdown();
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/admin/search")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Page<BatchEncResponseDto>>> searchBatchEncAdvanced(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "creationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PageValidator.validate(page, size);
        ApiResponse<Page<BatchEncResponseDto>> response = batchEncService.searchBatchEncAdvanced(
                description, status, fechaInicio, fechaFin, page, size, sortBy, sortDir
        );
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @DeleteMapping("/admin/delete/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Void>> softDeleteBatch(@PathVariable Long id) {
        ApiResponse<Void> response = batchEncService.softDeleteBatch(id);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }
}
