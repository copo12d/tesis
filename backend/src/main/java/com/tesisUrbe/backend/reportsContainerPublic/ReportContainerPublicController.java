package com.tesisUrbe.backend.reportsContainerPublic;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/reports/container-public")
public class ReportContainerPublicController {

    private final ReportContainerPublicService service;

    @GetMapping("/unnotified")
    public ResponseEntity<List<ReportContainerPublicResponseDto>> getUnnotified() {
        List<ReportContainerPublicResponseDto> dtos = service.getUnnotifiedReports()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<ReportContainerPublicResponseDto> createReport(@RequestBody ReportContainerPublicDto dto) {
        ReportContainerPublic saved = service.create(dto);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(toDto(saved));
    }

    @PostMapping("notify/{id}")
    public ResponseEntity<Void> markNotified(@PathVariable Long id) {
        service.markNotified(id);
        return ResponseEntity.noContent().build();
    }

    private ReportContainerPublicResponseDto toDto(ReportContainerPublic r) {
        return ReportContainerPublicResponseDto.builder()
                .id(r.getId())
                .containerId(r.getContainer() != null ? r.getContainer().getId() : null)
                .previousStatus(r.getPreviousStatus())
                .newStatus(r.getNewStatus())
                .fillLevel(r.getFillLevel())
                .message(r.getMessage())
                .createdAt(r.getCreatedAt())
                .notified(r.isNotified())
                .build();
    }
}
