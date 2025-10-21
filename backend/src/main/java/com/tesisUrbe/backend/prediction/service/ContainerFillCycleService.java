package com.tesisUrbe.backend.prediction.service;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.entities.account.User;
import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.prediction.dto.*;
import com.tesisUrbe.backend.prediction.model.CollectionCanceled;
import com.tesisUrbe.backend.prediction.model.ContainerFillCycle;
import com.tesisUrbe.backend.prediction.model.ContainerScheduler;
import com.tesisUrbe.backend.prediction.model.QrContainerFillNotice;
import com.tesisUrbe.backend.prediction.repository.CollectionCanceledRepository;
import com.tesisUrbe.backend.prediction.repository.ContainerFillCycleRepository;
import com.tesisUrbe.backend.prediction.repository.ContainerSchedulerRepository;
import com.tesisUrbe.backend.prediction.repository.QrContainerFillNoticeRepository;
import com.tesisUrbe.backend.solidWasteManagement.enums.ContainerStatus;
import com.tesisUrbe.backend.solidWasteManagement.repository.ContainerRepository;
import com.tesisUrbe.backend.usersManagement.exceptions.UserNotFoundException;
import com.tesisUrbe.backend.usersManagement.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContainerFillCycleService {

    private final ContainerFillCycleRepository containerFillCycleRepository;
    private final QrContainerFillNoticeRepository qrContainerFillNoticeRepository;
    private final ApiErrorFactory errorFactory;
    private final ContainerRepository containerRepository;
    private final CollectionCanceledRepository collectionCanceledRepository;
    private final UserService userService;
    private final ContainerSchedulerRepository containerSchedulerRepository;

    private final LocalTime minRecollectionTime = LocalTime.of(6,0,0);
    private final LocalTime maxRecollectionTime = LocalTime.of(22,0,0);

    /**
     * Procesa el reporte de que un contenedor ha alcanzado un nivel significativo de llenado.
     *
     * <p>Esta función realiza las siguientes acciones:</p>
     * <ul>
     * <li>Busca el contenedor por su número de serie, verificando que no esté marcado como eliminado.</li>
     * <li>Verifica que el contenedor no esté en estado de mantenimiento.</li>
     * <li>Obtiene y registra la dirección IP del cliente que realiza el reporte.</li>
     * <li>Implementa una lógica de antiflood, impidiendo reportes duplicados de la misma IP en la última hora.</li>
     * <li>Cuenta los reportes únicos de IP para el contenedor que aún no tienen un ciclo de llenado asignado.</li>
     * <li>Si la cuenta de reportes únicos alcanza 3 (o si el contenedor ya estaba marcado como LLENO), inicia un nuevo ciclo de llenado.</li>
     * <li>Guarda el registro de reporte (`QrContainerFillNotice`).</li>
     * </ul>
     *
     * @param serial  El número de serie único del contenedor que está siendo reportado.
     * @param request El objeto HttpServletRequest para obtener la dirección IP del reportero,
     *                priorizando el encabezado "X-Forwarded-For" sobre {@code getRemoteAddr}.
     * @return Un objeto {@code ApiResponse<Void>} indicando el resultado de la operación.
     * <ul>
     * <li>{@code 200 OK}: Reporte guardado y procesado exitosamente (si no se inició un nuevo ciclo).</li>
     * <li>{@code 404 NOT_FOUND}: El contenedor con el serial especificado no existe o está eliminado.</li>
     * <li>{@code 422 UNPROCESSABLE_ENTITY}: El contenedor está en mantenimiento ({@code UNDER_MAINTENANCE}).</li>
     * <li>{@code 429 TOO_MANY_REQUESTS}: Un reporte duplicado de la misma IP fue enviado en la última hora (Antiflood).</li>
     * <li>{@code 409 CONFLICT}: El contenedor ya ha alcanzado el umbral de reportes (>= 3) o ya está en estado LLENO,
     * indicando que el proceso de recolección está en curso o pendiente.</li>
     * </ul>
     */
    @Transactional
    public ApiResponse<Void> reportContainerBySerial(String serial, HttpServletRequest request) {
        Optional<Container> containerOpt = containerRepository.findBySerialAndDeletedFalse(serial);

        if (containerOpt.isEmpty()) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.NOT_FOUND, "Contenedor no encontrado"),
                    null,
                    List.of(new ApiError("CONTAINER_NOT_FOUND", "serial",
                            "Contenedor con el serial " + serial + " no fue encontrado"))
            );
        }

        Container container = containerOpt.get();

        if (ContainerStatus.UNDER_MAINTENANCE.equals(container.getStatus()))
            return errorFactory.build(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    List.of(new ApiError("UNDER_MAINTENANCE", null,
                            "El contanedor se encuentra en mantenimiento"))
            );


        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }

        boolean alreadyReported = qrContainerFillNoticeRepository
                .existsByContainerAndReporterIpAndTimeFillingNoticeAfter(
                        container, ip, LocalDateTime.now().minusHours(1));

        if (alreadyReported) {
            return errorFactory.build(
                    HttpStatus.TOO_MANY_REQUESTS,
                    List.of(new ApiError("DUPLICATE_REPORT", null,
                            "Ya has reportado este contenedor en la última hora"))
            );
        }

        long distinctReporters = qrContainerFillNoticeRepository
                .countDistinctReporterIpByContainerAndContainerFillCycleNull(container) + 1;

        if (distinctReporters > 3 || ContainerStatus.FULL.equals(container.getStatus())) {
            return errorFactory.build(
                    HttpStatus.CONFLICT,
                    List.of(new ApiError("FILLING_CYCLE_INCOMPLETE", null,
                            "Contenedor en proceso de recoleccion"))
            );
        }

        qrContainerFillNoticeRepository.save(
                QrContainerFillNotice.builder()
                        .container(container)
                        .reporterIp(ip)
                        .build());

        if (distinctReporters == 3) {
            fillContainerNotice(container);
        }

        return errorFactory.buildSuccess(HttpStatus.OK, "Contenedor lleno reportado exitosamente");

    }

    /**
     * Inicia un nuevo ciclo de notificación de llenado para un contenedor específico.
     *
     * <p>Esta función se ejecuta cuando se ha determinado que un contenedor ha alcanzado el umbral
     * de llenado (ej. suficientes reportes únicos) o manualmente se inicia un proceso de llenado
     * y realiza las siguientes tareas:</p>
     * <ul>
     * <li>Verifica que el contenedor no esté ya marcado como {@code FULL} (en proceso de recolección).</li>
     * <li>Calcula el número de llenado del día (`dayFillingNumber`) y el tiempo transcurrido
     * desde el ciclo de llenado anterior (`hoursBetweenFilling`).</li>
     * <li>Crea y persiste un nuevo registro {@code ContainerFillCycle}.</li>
     * <li>Asocia todos los reportes de llenado (`QrContainerFillNotice`) pendientes con el nuevo ciclo.</li>
     * <li>Actualiza el estado del contenedor a {@code ContainerStatus.FULL}.</li>
     * <li>Elimina todas las alertas programadas en el margen de 1 hora</li>
     * </ul>
     *
     * @param container La entidad {@code Container} para la cual se está iniciando el ciclo de llenado.
     * @return Un objeto {@code ApiResponse<Void>} que indica el resultado de la operación.
     * <ul>
     * <li>{@code 200 OK}: El proceso de recogida de contenedor lleno ha sido iniciado exitosamente.</li>
     * <li>{@code 409 CONFLICT}: Se devuelve si el contenedor ya estaba marcado como {@code FULL}
     * o si el último ciclo de llenado encontrado no tiene un registro de vaciado ({@code minutesToEmpty} es null),
     * indicando que el ciclo anterior no se completó correctamente.</li>
     * </ul>
     */
    @Transactional
    public ApiResponse<Void> fillContainerNotice(Container container) {

        if (ContainerStatus.FULL.equals(container.getStatus())) {
            return errorFactory.build(
                    HttpStatus.CONFLICT,
                    List.of(new ApiError("FILLING_CYCLE_INCOMPLETE", null,
                            "Contenedor en proceso de recoleccion"))
            );
        }

        Optional<ContainerFillCycle> lastNotice = containerFillCycleRepository
                .findTop1ByContainerAndDeletedFalseOrderByTimeFillingNoticeDesc(container);

        int fillingNumber;

        double hoursBetweenFilling;

        if (lastNotice.isEmpty()) {
            fillingNumber = 1;
            hoursBetweenFilling = 0;
        } else {
            ContainerFillCycle verifiedLastNotice = lastNotice.get();

            if (verifiedLastNotice.getMinutesToEmpty() == null) {
                return errorFactory.build(
                        HttpStatus.CONFLICT,
                        List.of(new ApiError("FILLING_CYCLE_INCOMPLETE", null,
                                "Proceso de recogida sin completar"))
                );
            }

            fillingNumber = (LocalDate.now().equals(verifiedLastNotice.getTimeFillingNotice().toLocalDate())) ?
                    verifiedLastNotice.getDayFillingNumber() + 1 : 1;

            long minutesDifference = ChronoUnit.MINUTES.between(
                    verifiedLastNotice.getTimeFillingNotice().plusMinutes(verifiedLastNotice.getMinutesToEmpty()),
                    LocalDateTime.now());
            hoursBetweenFilling = (double) minutesDifference / 60.0;
        }

        ContainerFillCycle newNotice = ContainerFillCycle.builder()
                .container(container)
                .dayFillingNumber(fillingNumber)
                .hoursBetweenFilling(hoursBetweenFilling)
                .build();

        ContainerFillCycle fillCycle = containerFillCycleRepository.save(newNotice);
        qrContainerFillNoticeRepository.linkNoticesToCycle(fillCycle, container);
        container.setStatus(ContainerStatus.FULL);
        containerRepository.save(container);
        containerSchedulerRepository.autoSuspendSchedulesByContainer(container, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        return errorFactory.buildSuccess(HttpStatus.OK, "Proceso de recogida de contenedor lleno iniciado exitosamente");
    }

    /**
     * Cancela el ciclo de llenado activo más reciente para un contenedor específico.
     *
     * <p>Esta función solo puede ser ejecutada por un usuario autenticado y realiza las siguientes acciones:</p>
     * <ul>
     * <li>Verifica la autenticación del usuario.</li>
     * <li>Busca la entidad {@code User} autenticada.</li>
     * <li>Busca el contenedor por su número de serie, asegurándose de que exista y no esté eliminado.</li>
     * <li>Busca el ciclo de llenado activo más reciente (aquel donde {@code deleted=false} y {@code minutesToEmpty} es nulo).</li>
     * <li>Marca el ciclo de llenado activo como eliminado (cancelado).</li>
     * <li>Revierte el estado del contenedor a {@code ContainerStatus.AVAILABLE}.</li>
     * <li>Guarda un registro en {@code CollectionCanceled} con el usuario, el ciclo y la razón de la cancelación.</li>
     * <li>Desuspende automáticamente (auto-unsuspend) los eventos de planificación futuros inmediatos (próxima hora) del contenedor.</li>
     * </ul>
     *
     * @param serial El número de serie único del contenedor cuyo ciclo de llenado debe ser cancelado.
     * @param reason El motivo por el cual se cancela el ciclo de llenado. Se utiliza "No se especifico el motivo de cancelación" si el texto es nulo o vacío.
     * @return Un objeto {@code ApiResponse<Void>} que indica el resultado de la operación.
     * <ul>
     * <li>{@code 200 OK}: La notificación ha sido cancelada exitosamente.</li>
     * <li>{@code 401 UNAUTHORIZED}: El usuario no está autenticado.</li>
     * <li>{@code 404 NOT_FOUND}: El contenedor con el serial especificado no existe o no se encontró un ciclo de llenado activo para cancelar.</li>
     * <li>{@code UserNotFoundException}: Lanzada si el usuario autenticado no puede ser encontrado en la base de datos (generalmente un error de configuración o estado).</li>
     * </ul>
     */
    @Transactional
    public ApiResponse<Void> cancelContainerNotice(String serial, String reason) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.UNAUTHORIZED, "No estás autenticado"),
                    null,
                    List.of(new ApiError("UNAUTHORIZED", null, "Debes iniciar sesión para generar el reporte"))
            );
        }

        User user = userService.findByUserName(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        Optional<Container> containerOpt = containerRepository.findBySerialAndDeletedFalse(serial);

        if (containerOpt.isEmpty()) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.NOT_FOUND, "Contenedor no encontrado"),
                    null,
                    List.of(new ApiError("CONTAINER_NOT_FOUND", "serial",
                            "Contenedor con el serial " + serial + " no fue encontrado"))
            );
        }

        Optional<ContainerFillCycle> lastNotice = containerFillCycleRepository
                .findByContainerAndDeletedFalseAndMinutesToEmptyNull(containerOpt.get());

        if (lastNotice.isEmpty())
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("FILL_NOTICE_NOT_FOUND",
                            null, "No se ha encontrado ninguna notificacion para cancelar"))
            );


        ContainerFillCycle lastCycle = lastNotice.get();
        lastCycle.setDeleted(true);
        Container container = containerOpt.get();
        container.setStatus(ContainerStatus.AVAILABLE);

        collectionCanceledRepository.save(
                CollectionCanceled.builder()
                        .containerFillCycle(lastCycle)
                        .user(user)
                        .reason((StringUtils.hasText(reason)) ? reason : "No se especifico el motivo de cancelación")
                        .build());
        containerFillCycleRepository.save(lastCycle);
        containerRepository.save(container);
        containerSchedulerRepository.autoUnsuspendSchedulesByContainer(container, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        return errorFactory.buildSuccess(HttpStatus.OK, "Notificacion cancelada exitosamente");
    }

    /**
     * Realiza una búsqueda avanzada de notificaciones de llenado de contenedores (QR)
     * aplicando diversos filtros opcionales.
     * * <p>El método requiere autenticación. Los resultados se mapean a {@code QrReportResponseDto}
     * para estandarizar la respuesta.</p>
     * * @param pageable Objeto de paginación que incluye el número de página, tamaño y ordenación.
     *
     * @param serial     Filtro opcional por número de serie del contenedor. Se utiliza un patrón de búsqueda 'like'.
     * @param reporterIp Filtro opcional por dirección IP del reportador. Se utiliza un patrón de búsqueda 'like'.
     * @param startDate  Filtro opcional por fecha y hora mínima de reporte (inclusive).
     * @param endDate    Filtro opcional por fecha y hora máxima de reporte (inclusive).
     * @param deleted    Filtro opcional para buscar por estado de eliminación del ciclo de llenado (true para cancelado, false para completo).
     * @return Un objeto {@code ApiResponse} que contiene una página de {@code QrReportResponseDto} si la búsqueda es exitosa,
     * o un error {@code UNAUTHORIZED} si el usuario no está autenticado.
     */
    @Transactional(readOnly = true)
    public ApiResponse<Page<QrReportResponseDto>> searchQrReport(
            Pageable pageable,
            String serial, String reporterIp,
            LocalDateTime startDate, LocalDateTime endDate,
            Boolean deleted
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.UNAUTHORIZED, "No estás autenticado"),
                    null,
                    List.of(new ApiError("UNAUTHORIZED", null, "Debes iniciar sesión para generar el reporte"))
            );
        }

        System.out.println(endDate);
        Page<QrReportProjection> reportPage = qrContainerFillNoticeRepository.searchQrReport(
                (StringUtils.hasText(serial)) ? '%' + serial + '%' : null,
                (StringUtils.hasText(reporterIp)) ? '%' + reporterIp + '%' : null,
                startDate, endDate, deleted, pageable);

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Búsqueda avanzada de usuarios completada"),
                new PageImpl<>(
                        reportPage.getContent().stream()
                                .map(qrReport -> QrReportResponseDto.builder()
                                        .containerSerial(qrReport.getContainerSerial())
                                        .reporterIp(qrReport.getReporterIp())
                                        .dayOfWeek(qrReport.getReportTime().getDayOfWeek())
                                        .month(qrReport.getReportTime().getMonth())
                                        .reportDate(qrReport.getReportTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                                        .reportTime(qrReport.getReportTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                                        .cycleStatus((qrReport.getDeleteCycle()) ? "Ciclo cancelado" : "Ciclo completo")
                                        .build())
                                .toList(),
                        reportPage.getPageable(),
                        reportPage.getTotalElements()),
                null
        );
    }

    //falta revision
    @Transactional
    public Optional<ApiResponse<Void>> completeFillingCycle(Container container) {
        Optional<ContainerFillCycle> lastNotice = containerFillCycleRepository
                .findTop1ByContainerAndDeletedFalseOrderByTimeFillingNoticeDesc(container);

        if (lastNotice.isEmpty()) return Optional.of(
                errorFactory.build(
                        HttpStatus.NOT_FOUND,
                        List.of(new ApiError("FILL_NOTICE_NOT_FOUND", null, "Ningun ciclo de recogida")))
        );

        ContainerFillCycle fillCycleData = lastNotice.get();
        if (fillCycleData.getMinutesToEmpty() != null || ContainerStatus.AVAILABLE.equals(container.getStatus()))
            return Optional.of(errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("FILLING_CYCLE_COMPLETE", null, "Ya se recogio el contenedor")))
            );

        long minutesToEmpty = ChronoUnit.MINUTES.between(fillCycleData.getTimeFillingNotice(), LocalDateTime.now());
        fillCycleData.setMinutesToEmpty(minutesToEmpty);
        container.setStatus(ContainerStatus.AVAILABLE);
        try {
            containerFillCycleRepository.save(fillCycleData);
            containerRepository.save(container);
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(errorFactory.build(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    List.of(new ApiError("PERSISTENCE_ERROR", null, "Error interno al registrar el aviso de contenedor lleno"))
            ));
        }
    }

    /**
     * Busca y recupera la información de la próxima recolección programada para un contenedor
     * específico, identificado por su número de serie.
     *
     * @param serial El número de serie del contenedor que se desea consultar.
     * @return Un objeto {@code ApiResponse<NextRecollectionDto>} que contiene:
     * <ul>
     * <li>Los datos de la próxima recolección programada si se encuentra y el contenedor está disponible (HTTP 200 OK).</li>
     * <li>Un error {@code NOT_FOUND} si el contenedor no existe.</li>
     * <li>Un error {@code UNPROCESSABLE_ENTITY} si el contenedor está en mantenimiento.</li>
     * <li>Un error {@code CONFLICT} si el contenedor ya está lleno y en proceso de recolección.</li>
     * </ul>
     */
    @Transactional(readOnly = true)
    public ApiResponse<NextRecollectionDto> nextRecollectionBySerial(String serial) {

        Optional<Container> containerOpt = containerRepository.findBySerialAndDeletedFalse(serial);

        if (containerOpt.isEmpty()) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.NOT_FOUND, "Contenedor no encontrado"),
                    null,
                    List.of(new ApiError("CONTAINER_NOT_FOUND", "serial",
                            "Contenedor con el serial " + serial + " no fue encontrado"))
            );
        }

        Container container = containerOpt.get();

        if (ContainerStatus.UNDER_MAINTENANCE.equals(container.getStatus()))
            return errorFactory.build(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    List.of(new ApiError("UNDER_MAINTENANCE", null,
                            "El contanedor se encuentra en mantenimiento"))
            );

        if (ContainerStatus.FULL.equals(container.getStatus())) {
            return errorFactory.build(
                    HttpStatus.CONFLICT,
                    List.of(new ApiError("FILLING_CYCLE_INCOMPLETE", null,
                            "Contenedor en proceso de recoleccion"))
            );
        }

        Optional<ContainerScheduler> nextRecollection = containerSchedulerRepository.findNextRecollectionByContainer(container);

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Próxima recolección obtenida exitosamente"),
                nextRecollection.map(containerScheduler -> NextRecollectionDto.builder()
                        .containerSerial(containerScheduler.getContainer().getSerial())
                        .nextRecollectionTime(containerScheduler.getSchedulerFillTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
                        .build()).orElse(null),
                null);
    }

    /**
     * Genera predicciones de horarios de recolección para un contenedor específico
     * basándose en su historial de ciclos de llenado.
     * <p>
     * El algoritmo utiliza un enfoque jerárquico para determinar la base de la predicción:
     * <ol>
     * <li>Intenta agrupar el historial por el **Día de la Semana y Mes** actual, requiriendo
     * {@code MIN_RECORDS_THRESHOLD} (5) registros por cada número de llenado diario.</li>
     * <li>Si los datos del punto 1 son insuficientes, retrocede y agrupa solo por el
     * **Día de la Semana** actual, manteniendo el mismo umbral.</li>
     * </ol>
     * Si no se alcanza el umbral de registros en ninguna agrupación, se considera que los
     * datos son insuficientes. Las predicciones calculadas se ajustan para respetar un
     * rango de horas mínimo y máximo de recolección.
     * </p>
     * * @param container El objeto {@code Container} para el cual se generarán las predicciones.
     * @return Una lista de {@code NewContainerSchedulerDto} que representa los horarios
     * de llenado predichos para el día actual. Retorna una lista vacía si los datos
     * históricos son insuficientes o no confiables.
     */
    public List<NewContainerSchedulerDto> createPrediction(Container container) {
        // 1. Obtener todos los datos históricos
        // Se asume que ahora trae todos los datos, sin filtrar por DayOfWeek o Month
        List<ContainerFillCycle> allHistoricalData = containerFillCycleRepository.getAllCycleData(container);

        // 2. Definir los criterios de tiempo actuales
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek currentDay = now.getDayOfWeek();
        Month currentMonth = now.getMonth();

        // El umbral mínimo de registros por grupo para hacer una predicción.
        final int MIN_RECORDS_THRESHOLD = 5;

        // --- PASO 3: Intentar la agrupación más específica (Día y Mes) ---
        // Clave: Map<FillingNumber, List<Cycle>>
        Map<Integer, List<ContainerFillCycle>> dataByDayMonth = allHistoricalData.stream()
                .filter(data -> {
                    // Filtra los registros que coinciden con el día y mes actual
                    LocalDateTime fillTime = data.getTimeFillingNotice();
                    return fillTime.getDayOfWeek() == currentDay && fillTime.getMonth() == currentMonth;
                })
                .collect(Collectors.groupingBy(ContainerFillCycle::getDayFillingNumber));

        // --- PASO 4: Intentar la agrupación menos específica (Solo Día) si no hay suficientes datos ---
        if (dataByDayMonth.isEmpty() || dataByDayMonth.values().stream().anyMatch(
                list -> list.size() < MIN_RECORDS_THRESHOLD)) {

            // Si no hay 3 registros por Mes/Día para cada número de llenado, retrocedemos al filtro solo por Día.
            dataByDayMonth = allHistoricalData.stream()
                    .filter(data -> data.getTimeFillingNotice().getDayOfWeek() == currentDay)
                    .collect(Collectors.groupingBy(ContainerFillCycle::getDayFillingNumber));

            // Si aún no tenemos suficientes registros (3 por cada 'DayFillingNumber' para el día actual)
            // o si el total de ciclos para el día es menor a 5 (como tu lógica original),
            // consideramos los datos insuficientes para una predicción confiable.
            if (dataByDayMonth.values().stream().mapToInt(List::size).sum() < 5 ||
                    dataByDayMonth.values().stream().anyMatch(list -> list.size() < MIN_RECORDS_THRESHOLD)) {

                // Si solo hay registros de un día, o hay menos de 3 por grupo
                return Collections.emptyList(); // Registros insuficientes o no confiables
            }
        }

        // --- PASO 5: Calcular el promedio de tiempo para cada DayFillingNumber ---
        Map<Integer, Double> averageFillTimeByNumber = dataByDayMonth.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(data -> {
                                    LocalDateTime fillTime = data.getTimeFillingNotice();
                                    // Convierte la hora a un valor double (ej: 10:30 -> 10.5)
                                    return fillTime.getHour() + (fillTime.getMinute() / 60.0);
                                })
                                .average()
                                .orElse(0.0) // Debería tener datos por la lógica de filtrado, pero por seguridad
                ));

        // --- PASO 6: Generar DTOs y Aplicar Restricciones de Tiempo ---
        return averageFillTimeByNumber.entrySet().stream()
                .map(newPrediction -> {
                    double totalHours = newPrediction.getValue();
                    long hours = (long) Math.floor(totalHours);
                    long minutes = Math.round((totalHours - hours) * 60.0);

                    // Ajuste simple de overflow (si los minutos dan 60 o más)
                    if (minutes >= 60) {
                        hours += 1;
                        minutes = 0;
                    }

                    // Establece la hora predicha en la fecha de hoy
                    LocalDateTime predictedTime = now.toLocalDate().atStartOfDay()
                            .plusHours(hours)
                            .plusMinutes(minutes);

                    // --- Aplicar restricciones de hora mínima y máxima ---
                    LocalTime predictedLocalTime = predictedTime.toLocalTime();

                    // Si la hora predicha está ANTES de la mínima
                    if (predictedLocalTime.isBefore(minRecollectionTime)) {
                        predictedTime = predictedTime.withHour(minRecollectionTime.getHour())
                                .withMinute(minRecollectionTime.getMinute());
                    }
                    // Si la hora predicha está DESPUÉS de la máxima
                    else if (predictedLocalTime.isAfter(maxRecollectionTime)) {
                        predictedTime = predictedTime.withHour(maxRecollectionTime.getHour())
                                .withMinute(maxRecollectionTime.getMinute());
                    }

                    return NewContainerSchedulerDto.builder()
                            .container(container)
                            .fillingNumber(newPrediction.getKey())
                            .schedulerFillTime(predictedTime)
                            .build();
                })
                .sorted(Comparator.comparingInt(NewContainerSchedulerDto::getFillingNumber))
                .toList();
    }
}
