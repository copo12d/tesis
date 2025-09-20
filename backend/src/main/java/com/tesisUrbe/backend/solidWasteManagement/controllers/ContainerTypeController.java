package com.tesisUrbe.backend.solidWasteManagement.controllers;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.solidWasteManagement.dto.ContainerTypeRequestDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.ContainerTypeResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.UpdateContainerTypeDto;
import com.tesisUrbe.backend.solidWasteManagement.services.ContainerTypeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("${api.base-path}/container-type")
public class ContainerTypeController {

    private ContainerTypeService containerTypeService;

    @PostMapping("/admin/register")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Void>> registerContainerType(@Valid @RequestBody ContainerTypeRequestDto dto) {
        ApiResponse<Void> response = containerTypeService.registerContainerType(dto);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<ContainerTypeResponseDto>> getContainerTypeById(@PathVariable Long id) {
        ApiResponse<ContainerTypeResponseDto> response = containerTypeService.getContainerTypeById(id);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Page<ContainerTypeResponseDto>>> getAllContainerTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "") String search) {

        ApiResponse<Page<ContainerTypeResponseDto>> response = containerTypeService.getAllContainerTypes(page, size, sortBy, sortDir, search);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @PutMapping("/admin/update/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Void>> updateContainerType(
            @PathVariable Long id,
            @Valid @RequestBody UpdateContainerTypeDto dto) {
        ApiResponse<Void> response = containerTypeService.updateContainerType(id, dto);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @DeleteMapping("/admin/delete/{id}")
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Void>> softDeleteContainerType(@PathVariable Long id) {
        ApiResponse<Void> response = containerTypeService.softDeleteContainerType(id);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

}
