package com.tesisUrbe.backend.prediction.controller;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.prediction.dto.AverageTimeResponseDto;
import com.tesisUrbe.backend.prediction.service.ContainerFillCycleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/container")
public class ContainerReportManagerController {

    private final ContainerFillCycleService containerFillCycleService;

    //funcional
    @PostMapping("/public/report")
    public ResponseEntity<ApiResponse<Void>> reportContainerFull(@RequestParam String serial, HttpServletRequest request) {
        ApiResponse<Void> response = containerFillCycleService.reportContainerBySerial(serial, request);

        return ResponseEntity.status(response.meta().status()).body(response);
    }

    @PostMapping("/employee/recollect-cancel")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<ApiResponse<Void>> cancelContainerFillCycle(
        @RequestParam String serial, 
        //Si es mucho se borra
        @RequestBody String reason){

        ApiResponse<Void> response = containerFillCycleService.cancelContainerNotice(serial, reason);

        return ResponseEntity.status(response.meta().status()).body(response);
    }

    @GetMapping("/admin/recollect-time/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<AverageTimeResponseDto>> avaregeRecollectTime(){

        ApiResponse<AverageTimeResponseDto> response = containerFillCycleService.completeAverageTime();

        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

}
