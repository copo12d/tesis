package com.tesisUrbe.backend.prediction.controller;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.prediction.service.ContainerFillCycleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/container/report")
public class ContainerReportManagerController {

    private final ContainerFillCycleService containerFillCycleService;

    @PostMapping()
    public ResponseEntity<?> reportContainerFull(@RequestParam String serial, HttpServletRequest request) {
        Optional<ApiResponse<Void>> result = containerFillCycleService.reportContainerBySerial(serial, request);

        return result.map(response -> ResponseEntity.status(response.meta().status()
                ).body(response))
                .orElseGet(() -> ResponseEntity.ok().build());
    }

}
