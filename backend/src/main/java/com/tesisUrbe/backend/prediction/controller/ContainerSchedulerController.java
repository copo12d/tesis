package com.tesisUrbe.backend.prediction.controller;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.prediction.dto.ManualSchedulerDto;
import com.tesisUrbe.backend.prediction.model.ContainerScheduler;
import com.tesisUrbe.backend.prediction.service.ContainerFillCycleService;
import com.tesisUrbe.backend.prediction.service.ContainerSchedulerService;
import com.tesisUrbe.backend.solidWasteManagement.services.ContainerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/scheduler")
public class ContainerSchedulerController {

    private final ContainerSchedulerService containerSchedulerService;

    @PostMapping("/admin/prediction")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<List<List<ContainerScheduler>>>> createdSchedulerPrediction(
            @Valid @RequestBody List<String> containers
    ){
        ApiResponse<List<List<ContainerScheduler>>> response = containerSchedulerService.schedulerPredictions(containers);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @PostMapping("/admin/manual")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<List<List<ContainerScheduler>>>> createdSchedulerManual(
            @Valid @RequestBody List<ManualSchedulerDto> manualScheduler
            ){
        ApiResponse<List<List<ContainerScheduler>>> response = containerSchedulerService.schedulerManual(manualScheduler);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }
}
