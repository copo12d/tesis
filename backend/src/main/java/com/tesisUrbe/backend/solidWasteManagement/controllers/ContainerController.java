package com.tesisUrbe.backend.solidWasteManagement.controllers;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.solidWasteManagement.dto.ContainerRequestDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.ContainerResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.services.ContainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

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

    @PutMapping("/admin/update/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Void>> updateContainer(
            @PathVariable Long id,
            @Valid @RequestBody ContainerRequestDto dto) {

        ApiResponse<Void> response = containerService.updateContainer(id, dto);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @DeleteMapping("/admin/delete/{id}")
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Void>> softDeleteContainer(@PathVariable Long id) {
        ApiResponse<Void> response = containerService.softDeleteContainer(id);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/admin/qr/{id}")
    public ResponseEntity<ApiResponse<byte[]>> getContainerQrById(@PathVariable Long id) {
        ApiResponse<byte[]> response = containerService.generateContainerQrById(id);

        if (response.data() == null || response.data().length == 0) {
            return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
