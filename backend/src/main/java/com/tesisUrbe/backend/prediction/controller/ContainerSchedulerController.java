package com.tesisUrbe.backend.prediction.controller;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.prediction.dto.ManualSchedulerDto;
import com.tesisUrbe.backend.prediction.dto.NextRecollectionDto;
import com.tesisUrbe.backend.prediction.model.ContainerScheduler;
import com.tesisUrbe.backend.prediction.service.ContainerSchedulerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/scheduler")
public class ContainerSchedulerController {

    private final ContainerSchedulerService containerSchedulerService;

    @PostMapping("/admin/prediction")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<List<List<ContainerScheduler>>>> createdSchedulerPrediction(
           @RequestBody List<String> containers
    ) {
        ApiResponse<List<List<ContainerScheduler>>> response = containerSchedulerService.schedulerPredictions(containers);

        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    
    @PostMapping("/admin/manual")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<List<NextRecollectionDto>>> createdSchedulerManual(
            @Valid @RequestBody ManualSchedulerDto manualScheduler
    ) {
        ApiResponse<List<NextRecollectionDto>> response = containerSchedulerService.schedulerManual(manualScheduler);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    
    @GetMapping("/employee/next-recollection")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERUSER', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<Page<NextRecollectionDto>>> getNextRecollectionForAllContainer(
            @RequestParam(required = false) String serial,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "serial") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir

    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        ApiResponse<Page<NextRecollectionDto>> response = containerSchedulerService.searchNextRecollectionPage(
                serial, pageable);

        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    
    @GetMapping("/employee/next-recollection/{serial}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERUSER', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<NextRecollectionDto>>> containerScheduler(
            @PathVariable("serial") String serial
    ) {

        ApiResponse<List<NextRecollectionDto>> response = containerSchedulerService.nextRecollectionsBySerial(serial);

        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }


    @PutMapping("/admin/suspend/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERUSER')")
    public ResponseEntity<ApiResponse<Void>> suspendScheduler(
            @PathVariable("id") Long id
    ){
        ApiResponse<Void> response = containerSchedulerService.adminSchedulerSuspension(id);

        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

}
