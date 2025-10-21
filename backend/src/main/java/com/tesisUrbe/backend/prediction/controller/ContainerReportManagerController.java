package com.tesisUrbe.backend.prediction.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.prediction.dto.NextRecollectionDto;
import com.tesisUrbe.backend.prediction.dto.QrReportResponseDto;
import com.tesisUrbe.backend.prediction.service.ContainerFillCycleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base-path}/container")
public class ContainerReportManagerController {

    private final ContainerFillCycleService containerFillCycleService;

    /**
     * Endpoint público para que un usuario (o un sistema anónimo) reporte que un contenedor
     * ha alcanzado su capacidad máxima o que requiere atención de llenado.
     *
     * <p>Este método es accesible sin autenticación (ruta '/public/report').</p>
     *
     * @param serial  El número de serie único (String) del contenedor que está siendo reportado como lleno.
     *                Se espera que este parámetro se envíe como un parámetro de consulta (query parameter).
     * @param request El objeto HttpServletRequest que contiene información sobre la solicitud,
     *                utilizado principalmente para obtener la dirección IP del cliente que realiza el reporte.
     * @return Un {@code ResponseEntity<ApiResponse<Void>>} con el resultado de la operación.
     * El cuerpo de la respuesta contendrá un {@code ApiResponse} sin datos ({@code Void}).
     * <ul>
     * <li>{@code 200 OK}: Reporte guardado y procesado exitosamente (si no se inició un nuevo ciclo).</li>
     * <li>{@code 404 NOT_FOUND}: El contenedor con el serial especificado no existe o está eliminado.</li>
     * <li>{@code 422 UNPROCESSABLE_ENTITY}: El contenedor está en mantenimiento ({@code UNDER_MAINTENANCE}).</li>
     * <li>{@code 429 TOO_MANY_REQUESTS}: Un reporte duplicado de la misma IP fue enviado en la última hora (Antiflood).</li>
     * <li>{@code 409 CONFLICT}: El contenedor ya ha alcanzado el umbral de reportes (>= 3) o ya está en estado LLENO,
     * indicando que el proceso de recolección está en curso o pendiente.</li>
     * </ul>
     */
    @PostMapping("/public/report")
    public ResponseEntity<ApiResponse<Void>> reportContainerFull(@RequestParam String serial, HttpServletRequest request) {
        ApiResponse<Void> response = containerFillCycleService.reportContainerBySerial(serial, request);

        return ResponseEntity.status(response.meta().status()).body(response);
    }

    /**
     * Endpoint para que un empleado o administrador cancele un ciclo de notificación de llenado activo
     * para un contenedor específico.
     *
     * <p>Este método requiere autenticación y autorización. Solo los usuarios con los roles
     * {@code ROLE_ADMIN}, {@code ROLE_SUPERUSER}, o {@code ROLE_EMPLOYEE} pueden acceder.</p>
     *
     * <p>La cancelación revierte el estado del contenedor a "Disponible" y registra el motivo de la cancelación.</p>
     *
     * @param serial El número de serie único del contenedor cuyo ciclo de llenado activo se desea cancelar.
     *               Se espera como un parámetro de consulta (query parameter).
     * @param reason El cuerpo de la solicitud (JSON) que debe contener el motivo de la cancelación.
     *               Es un mapa donde se espera la clave "reason" (ej. {@code {"reason": "Motivo de la cancelación"}}).
     * @return Un {@code ResponseEntity<ApiResponse<Void>>} con el resultado de la operación.
     * <ul>
     * <li>Código de estado HTTP 200 (OK) si la notificación fue cancelada exitosamente.</li>
     * <li>Código de estado HTTP 401 (Unauthorized) si el usuario no está autenticado.</li>
     * <li>Código de estado HTTP 403 (Forbidden) si el usuario no tiene los roles requeridos.</li>
     * <li>Código de estado HTTP 404 (Not Found) si el contenedor no existe o no hay un ciclo activo para cancelar.</li>
     * </ul>
     */
    @PostMapping("/employee/recollect-cancel")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<ApiResponse<Void>> cancelContainerFillCycle(
            @RequestParam String serial,
            @RequestBody Map<String, String> reason) {

        ApiResponse<Void> response = containerFillCycleService.cancelContainerNotice(
                serial,
                reason.getOrDefault("reason", null));

        return ResponseEntity.status(response.meta().status()).body(response);
    }

    /**
     * Permite a los usuarios con rol de 'ADMIN' o 'SUPERUSER' realizar búsquedas avanzadas y paginadas
     * de los reportes de llenado de contenedores (QR).
     * @param serial Filtro opcional por el número de serie del contenedor (búsqueda parcial).
     * @param reporterIp Filtro opcional por la dirección IP del dispositivo que generó el reporte.
     * @param startDate Filtro opcional para la fecha y hora de inicio del rango de búsqueda (inclusive),
     * esperado en formato ISO 8601 (e.g., yyyy-MM-ddTHH:mm:ss).
     * @param endDate Filtro opcional para la fecha y hora de fin del rango de búsqueda (inclusive),
     * esperado en formato ISO 8601.
     * @param deleted Filtro opcional por el estado de eliminación/cancelación del ciclo de llenado.
     * @param page Número de página a recuperar (por defecto: 0).
     * @param size Cantidad de registros por página (por defecto: 10).
     * @param sortBy Campo por el cual ordenar los resultados (por defecto: reportTime).
     * @param sortDir Dirección de la ordenación (ASC o DESC, por defecto: DESC).
     * @return Un objeto {@code ResponseEntity} que contiene un {@code ApiResponse} con la página
     * de resultados {@code QrReportResponseDto} y el estado HTTP.
     */
    @GetMapping("/admin/qr-reports")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Page<QrReportResponseDto>>> searchQrReports(
            @RequestParam(required = false) String serial,
            @RequestParam(required = false) String reporterIp,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Boolean deleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "reportTime") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        ApiResponse<Page<QrReportResponseDto>> response = containerFillCycleService.searchQrReport(
                pageable, serial, reporterIp, startDate, endDate, deleted);

        return ResponseEntity.status(response.meta().status()).body(response);
    }

    /**
     * Obtiene la información del próximo ciclo de llenado (recolección) para un contenedor específico
     * utilizando su número de serie.
     * <p>
     * Este es un endpoint público, lo que significa que no requiere autenticación de usuario.
     * </p>
     *
     * @param serial El número de serie único del contenedor a consultar, extraído de la ruta (PathVariable).
     * @return Un objeto {@code ResponseEntity} que contiene un {@code ApiResponse} con:
     * Un objeto {@code NextRecollectionDto} con los detalles del próximo ciclo de recolección (si se encuentra).
     */
    @GetMapping("/public/next-recollection/{serial}")
    public ResponseEntity<ApiResponse<NextRecollectionDto>> publicNextRecollection(@PathVariable("serial") String serial) {
        ApiResponse<NextRecollectionDto> response = containerFillCycleService.nextRecollectionBySerial(serial);

        return ResponseEntity.status(response.meta().status()).body(response);
    }

}
