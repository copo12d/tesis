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

    /**
     * Inicia el proceso de generación y persistencia de predicciones de horarios de recolección
     * para una lista de contenedores.
     * <p>
     * Este *endpoint* está restringido a usuarios con los roles 'ROLE_ADMIN' o 'ROLE_SUPERUSER'.
     * El cuerpo de la solicitud debe contener una lista JSON de números de serie de contenedores.
     * </p>
     * El método delega la lógica de negocio a la capa de servicio, la cual intentará generar
     * nuevos cronogramas basados en datos históricos para cada serial proporcionado.
     *
     * @param containers Una lista de {@code String} que representan los números de serie
     * de los contenedores para los cuales se deben crear las predicciones.
     * @return Un objeto {@code ResponseEntity} que envuelve un {@code ApiResponse}. El cuerpo
     * contiene una lista de listas de {@code ContainerScheduler}, que son los nuevos
     * cronogramas guardados, junto con metadatos y posibles errores acumulados
     * durante el procesamiento (ej. contenedores no encontrados o con datos insuficientes).
     */
    @PostMapping("/admin/prediction")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<List<List<ContainerScheduler>>>> createdSchedulerPrediction(
           @RequestBody List<String> containers
    ) {
        ApiResponse<List<List<ContainerScheduler>>> response = containerSchedulerService.schedulerPredictions(containers);

        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    /**
     * Crea un nuevo conjunto de cronogramas de recolección (schedulers) de forma manual
     * para un contenedor específico.
     * <p>
     * Este *endpoint* está restringido a usuarios con los roles 'ROLE_ADMIN' o 'ROLE_SUPERUSER'.
     * La solicitud requiere un {@code ManualSchedulerDto} que debe ser validado y que
     * contiene el serial del contenedor y la lista de horarios a programar.
     * </p>
     *
     * @param manualScheduler Objeto {@code ManualSchedulerDto} validado que contiene los detalles
     * del contenedor y los horarios programados a insertar.
     * @return Un objeto {@code ResponseEntity} que encapsula un {@code ApiResponse}. El cuerpo
     * contiene una lista de {@code NextRecollectionDto} con los cronogramas que fueron creados
     * exitosamente. El estado HTTP reflejará el resultado de la operación (ej., 200 OK si es exitoso,
     * o 400 Bad Request si el contenedor no es válido).
     */
    @PostMapping("/admin/manual")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<List<NextRecollectionDto>>> createdSchedulerManual(
            @Valid @RequestBody ManualSchedulerDto manualScheduler
    ) {
        ApiResponse<List<NextRecollectionDto>> response = containerSchedulerService.schedulerManual(manualScheduler);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    /**
     * Recupera de forma paginada y ordenada las próximas recolecciones programadas
     * para todos los contenedores.
     * <p>
     * Este *endpoint* está restringido a usuarios con los roles 'ADMIN', 'SUPERUSER' o 'EMPLOYEE'.
     * Permite filtrar los resultados opcionalmente por el número de serie del contenedor.
     * </p>
     *
     * @param serial Filtro opcional por el número de serie del contenedor (búsqueda parcial).
     * @param page El número de página a recuperar (por defecto: 0).
     * @param size El tamaño de la página (cantidad de registros por página, por defecto: 10).
     * @param sortBy El campo por el cual ordenar los resultados (por defecto: serial).
     * @param sortDir La dirección de la ordenación (ASC o DESC, por defecto: DESC).
     * @return Un objeto {@code ResponseEntity} que encapsula un {@code ApiResponse} con una
     * página de {@code NextRecollectionDto}, incluyendo el estado HTTP y los metadatos de la paginación.
     */
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

    //Listo
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
