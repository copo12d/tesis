package com.tesisUrbe.backend.prediction.controller;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.prediction.dto.AverageTimeResponseDto;
import com.tesisUrbe.backend.prediction.service.ContainerFillCycleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/container")
public class ContainerReportManagerController {

    private final ContainerFillCycleService containerFillCycleService;

    @PostMapping("/public/report")
    public ResponseEntity<?> reportContainerFull(@RequestParam String serial, HttpServletRequest request) {
        Optional<ApiResponse<Void>> result = containerFillCycleService.reportContainerBySerial(serial, request);

        return result.map(response -> ResponseEntity.status(response.meta().status()
                ).body(response))
                .orElseGet(() -> ResponseEntity.ok().build());
    }

    @GetMapping("/admin/recollect-time/all")
    public ResponseEntity<ApiResponse<AverageTimeResponseDto>> avaregeRecollectTime(){

        ApiResponse<AverageTimeResponseDto> response = containerFillCycleService.completeAverageTime();

        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

}
