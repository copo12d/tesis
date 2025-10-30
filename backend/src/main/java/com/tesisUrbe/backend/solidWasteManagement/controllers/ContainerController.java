package com.tesisUrbe.backend.solidWasteManagement.controllers;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.solidWasteManagement.dto.*;
import com.tesisUrbe.backend.solidWasteManagement.services.ContainerService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/container")
public class ContainerController {

    private final ContainerService containerService;

    @PostMapping("/admin/register")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Void>> registerContainer(@Valid @RequestBody ContainerRequestDto dto) {
        ApiResponse<Void> response = containerService.registerContainer(dto);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<ContainerResponseDto>> getContainerById(@PathVariable Long id) {
        ApiResponse<ContainerResponseDto> response = containerService.getContainerById(id);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/public/{id}")
    @PermitAll
    public ResponseEntity<ApiResponse<ContainerResponseDto>> getPublicContainerById(@PathVariable Long id) {
        ApiResponse<ContainerResponseDto> response = containerService.getContainerById(id);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/public/full/count")
    @PermitAll
    public ResponseEntity<ApiResponse<Long>> getPublicFullContainerCount() {
        ApiResponse<Long> response = containerService.getFullContainerCount();
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Page<ContainerResponseDto>>> getAllContainers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String serial,
            @RequestParam(required = false) Long id) {

        ApiResponse<Page<ContainerResponseDto>> response =
                containerService.getAllContainers(page, size, sortBy, sortDir, serial, id);

        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/public/count")
    public ResponseEntity<ApiResponse<Long>> getPublicActiveContainerCount() {
        ApiResponse<Long> response = containerService.getActiveContainerCount();

        return ResponseEntity
                .status(HttpStatus.valueOf(response.meta().status()))
                .body(response);
    }

    @GetMapping("/admin/qr/{id}")
    public ResponseEntity<ApiResponse<byte[]>> getContainerQrById(@PathVariable Long id) {
        ApiResponse<byte[]> response = containerService.generateContainerQrById(id);

        if (response.data() == null || response.data().length == 0) {
            return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/admin/alerts/full")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<List<ContainerAlertDto>>> getFullContainerAlerts() {
        ApiResponse<List<ContainerAlertDto>> response = containerService.getFullContainerAlerts();
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/public/active-summary")
    public ResponseEntity<ApiResponse<List<ContainerTypeSummaryDto>>> getActiveContainerSummary() {
        ApiResponse<List<ContainerTypeSummaryDto>> response = containerService.getActiveContainerCounts();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/public/full-summary")
    public ResponseEntity<ApiResponse<List<ContainerTypeSummaryDto>>> getFullContainerSummary() {
        ApiResponse<List<ContainerTypeSummaryDto>> response = containerService.getFullContainerSummary();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/admin/update/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Void>> updateContainer(
            @PathVariable Long id,
            @Valid @RequestBody ContainerUpdateDto dto) {

        ApiResponse<Void> response = containerService.updateContainer(id, dto);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @DeleteMapping("/admin/delete/{id}")
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Void>> softDeleteContainer(@PathVariable Long id) {
        ApiResponse<Void> response = containerService.softDeleteContainer(id);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

}
