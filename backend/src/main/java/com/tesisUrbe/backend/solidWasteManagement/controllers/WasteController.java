package com.tesisUrbe.backend.solidWasteManagement.controllers;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.solidWasteManagement.dto.WasteRequestDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.WasteResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.services.WasteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/waste")
public class WasteController {

    private final WasteService wasteService;

    @PostMapping("/admin/register")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Void>> registerWaste(@Valid @RequestBody WasteRequestDto dto) {
        ApiResponse<Void> response = wasteService.registerWaste(dto);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<WasteResponseDto>> getWasteById(@PathVariable Long id) {
        ApiResponse<WasteResponseDto> response = wasteService.getWasteById(id);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Page<WasteResponseDto>>> getAllWaste(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "collectionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String search) {
        ApiResponse<Page<WasteResponseDto>> response = wasteService.getAllWaste(page, size, sortBy, sortDir, search);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }
}
