package com.tesisUrbe.backend.prediction.controller;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.prediction.dto.ManualSchedulerDto;
import com.tesisUrbe.backend.prediction.model.ContainerScheduler;
import com.tesisUrbe.backend.prediction.service.ContainerSchedulerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión y creación de cronogramas de llenado de contenedores.
 * * <p>Proporciona endpoints para generar predicciones automáticas basadas en datos históricos
 * y para registrar cronogramas de forma manual por administradores.</p>
 * @author José
 * @version 1.0
 * @since 2025-10-05
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/scheduler")
public class ContainerSchedulerController {

    private final ContainerSchedulerService containerSchedulerService;

    /**
     * Endpoint para generar un cronograma de llenado (scheduler) basado en predicciones automáticas.
     * * <p>Requiere los roles 'ROLE_ADMIN' o 'ROLE_SUPERUSER'. El proceso utiliza datos históricos
     * de llenado para predecir las horas en las que los contenedores se llenarán hoy.</p>
     * @param containers Lista de seriales de los contenedores para los que se generarán las predicciones.
     * @return ResponseEntity conteniendo un objeto ApiResponse con la lista de cronogramas creados
     * y el estado HTTP correspondiente.
     * @see ContainerSchedulerService#schedulerPredictions(List)
     */
    @PostMapping("/admin/prediction")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<List<List<ContainerScheduler>>>> createdSchedulerPrediction(
            @Valid @RequestBody List<String> containers
    ){
        ApiResponse<List<List<ContainerScheduler>>> response = containerSchedulerService.schedulerPredictions(containers);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    /**
     * Endpoint para registrar manualmente un cronograma de llenado (scheduler).
     * * <p>Permite a los administradores o superusuarios ingresar fechas y horas específicas
     * para el llenado de contenedores, anulando o complementando las predicciones automáticas.</p>
     * * @param manualScheduler Lista de objetos ManualSchedulerDto con los datos del cronograma manual.
     * @return ResponseEntity conteniendo un objeto ApiResponse con la lista de cronogramas creados
     * y el estado HTTP correspondiente.
     * @see ContainerSchedulerService
     */
    @PostMapping("/admin/manual")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<List<List<ContainerScheduler>>>> createdSchedulerManual(
            @Valid @RequestBody List<ManualSchedulerDto> manualScheduler
            ){
        ApiResponse<List<List<ContainerScheduler>>> response = containerSchedulerService.schedulerManual(manualScheduler);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }
}
