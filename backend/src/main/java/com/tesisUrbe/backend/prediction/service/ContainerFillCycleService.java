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

    //Pasar a university Settings
    private final LocalTime minRecollectionTime = LocalTime.of(6,0,0);
    private final LocalTime maxRecollectionTime = LocalTime.of(22,0,0);

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

   
    public List<NewContainerSchedulerDto> createPrediction(Container container) {
        
        List<ContainerFillCycle> allHistoricalData = containerFillCycleRepository.getAllCycleData(container);

        LocalDateTime now = LocalDateTime.now();
        DayOfWeek currentDay = now.getDayOfWeek();
        Month currentMonth = now.getMonth();

        // El umbral mínimo de registros por grupo para hacer una predicción.
        final int MIN_RECORDS_THRESHOLD = 5;


        Map<Integer, List<ContainerFillCycle>> dataByDayMonth = allHistoricalData.stream()
                .filter(data -> {
                    // Filtra los registros que coinciden con el día y mes actual
                    LocalDateTime fillTime = data.getTimeFillingNotice();
                    return fillTime.getDayOfWeek() == currentDay && fillTime.getMonth() == currentMonth;
                })
                .collect(Collectors.groupingBy(ContainerFillCycle::getDayFillingNumber));

        // Intentar la agrupación menos específica (Solo Día) si no hay suficientes datos ---
        if (dataByDayMonth.isEmpty() || dataByDayMonth.values().stream().anyMatch(
                list -> list.size() < MIN_RECORDS_THRESHOLD)) {

            // Si no hay 3 registros por Mes/Día para cada número de llenado, retrocedemos al filtro solo por Día.
            dataByDayMonth = allHistoricalData.stream()
                    .filter(data -> data.getTimeFillingNotice().getDayOfWeek() == currentDay)
                    .collect(Collectors.groupingBy(ContainerFillCycle::getDayFillingNumber));

            
            if (dataByDayMonth.values().stream().mapToInt(List::size).sum() < 5 ||
                    dataByDayMonth.values().stream().anyMatch(list -> list.size() < MIN_RECORDS_THRESHOLD)) {

                // Si solo hay registros de un día, o hay menos de 3 por grupo
                return Collections.emptyList(); 
            }
        }

        //Calcular el promedio de tiempo para cada DayFillingNumber 
        Map<Integer, Double> averageFillTimeByNumber = dataByDayMonth.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(data -> {
                                    LocalDateTime fillTime = data.getTimeFillingNotice();
                                    return fillTime.getHour() + (fillTime.getMinute() / 60.0);
                                })
                                .average()
                                .orElse(0.0) // Debería tener datos por la lógica de filtrado, pero por seguridad
                ));


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

                    LocalDateTime predictedTime = now.toLocalDate().atStartOfDay()
                            .plusHours(hours)
                            .plusMinutes(minutes);

                    // --- Aplicar restricciones de hora mínima y máxima ---
                    LocalTime predictedLocalTime = predictedTime.toLocalTime();

          
                    if (predictedLocalTime.isBefore(minRecollectionTime)) {
                        predictedTime = predictedTime.withHour(minRecollectionTime.getHour())
                                .withMinute(minRecollectionTime.getMinute());
                    }
           
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
