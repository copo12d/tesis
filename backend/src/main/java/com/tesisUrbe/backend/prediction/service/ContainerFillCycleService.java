package com.tesisUrbe.backend.prediction.service;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.entities.account.User;
import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.prediction.dto.AverageTimeResponseDto;
import com.tesisUrbe.backend.prediction.dto.ContainerAverageTime;
import com.tesisUrbe.backend.prediction.dto.ContainerRecollectTimeProyection;
import com.tesisUrbe.backend.prediction.dto.NewContainerSchedulerDto;
import com.tesisUrbe.backend.prediction.dto.QrReportProyection;
import com.tesisUrbe.backend.prediction.dto.QrReportResponseDto;
import com.tesisUrbe.backend.prediction.model.CollectionCanceled;
import com.tesisUrbe.backend.prediction.model.ContainerFillCycle;
import com.tesisUrbe.backend.prediction.model.QrContainerFillNotice;
import com.tesisUrbe.backend.prediction.repository.CollectionCanceledRepository;
import com.tesisUrbe.backend.prediction.repository.ContainerFillCycleRepository;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
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

    //funcional
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

        if(ContainerStatus.UNDER_MAINTENANCE.equals(container.getStatus()))
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

        if(distinctReporters > 3 || ContainerStatus.FULL.equals(container.getStatus())){
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

        if(distinctReporters == 3){
            fillContainerNotice(container);
        }

        return errorFactory.buildSuccess(HttpStatus.OK, "Contenedor lleno reportado exitosamente");

    }

    //funcional
    @Transactional
    public ApiResponse<Void> fillContainerNotice(Container container) {

        if(ContainerStatus.FULL.equals(container.getStatus())){
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

        return errorFactory.buildSuccess(HttpStatus.OK, "Proceso de recogida de contenedor lleno iniciado exitosamente");
    }

    //funcional
    @Transactional
    public ApiResponse<Void> cancelContainerNotice(String serial, String reason){
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

        if(lastNotice.isEmpty())
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

        return errorFactory.buildSuccess(HttpStatus.OK, "Notificacion cancelada exitosamente");
    }


    @Transactional(readOnly = true)
    public ApiResponse<Page<QrReportResponseDto>> searchQrReport(
        Pageable pageable, 
        String serial, String reporterIp, 
        LocalDateTime startDate, LocalDateTime endDate,
        DayOfWeek dayOfWeek, Month month,
        Boolean deleted
    ){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.UNAUTHORIZED, "No estás autenticado"),
                    null,
                    List.of(new ApiError("UNAUTHORIZED", null, "Debes iniciar sesión para generar el reporte"))
            );
        }

        Page<QrReportProyection> reportPage = qrContainerFillNoticeRepository.searchQrReport(
            (StringUtils.hasText(serial)) ? serial : null, 
            (StringUtils.hasText(reporterIp)) ? reporterIp : null, 
            startDate, endDate,  dayOfWeek, month, deleted, pageable);

        Page<QrReportResponseDto> filteredPage = new PageImpl<>(
                reportPage.getContent().stream()
                        .map(qrReport -> QrReportResponseDto.builder()
                        .containerSerial(qrReport.getContainerSerial())
                        .reporterIp(qrReport.getReporterIp())
                        .dayOfWeek(qrReport.getReportTime().getDayOfWeek())
                        .month(qrReport.getReportTime().getMonth())
                        .reportDate(qrReport.getReportTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                        .reportTime(qrReport.getReportTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                        .cycleStatus((qrReport.getDeleteCycle()) ? "Ciclo cancelado" :  "Ciclo completo" )
                        .build())
                        .toList(),
                reportPage.getPageable(),
                reportPage.getTotalElements()
            );



        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Búsqueda avanzada de usuarios completada"),
                filteredPage,
                null
        );
    }

    @Transactional
    public Optional<ApiResponse<Void>> completeFillingCycle(Container container){
        Optional<ContainerFillCycle> lastNotice = containerFillCycleRepository
                .findTop1ByContainerAndDeletedFalseOrderByTimeFillingNoticeDesc(container);
        if(lastNotice.isEmpty()) return Optional.of(
                errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("FILL_NOTICE_NOT_FOUND", null, "No se ha encontrado ninguna notificacion pendiente")))
        );
        ContainerFillCycle fillCycleData = lastNotice.get();
        if(fillCycleData.getMinutesToEmpty() != null)
            return Optional.of(errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("FILLING_CYCLE_COMPLETE", null, "Ya se recogio el contenedor")))
            );
        long minutesToEmpty = ChronoUnit.MINUTES.between(fillCycleData.getTimeFillingNotice(), LocalDateTime.now());
        fillCycleData.setMinutesToEmpty(minutesToEmpty);
        try {
            containerFillCycleRepository.save(fillCycleData);
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(errorFactory.build(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    List.of(new ApiError("PERSISTENCE_ERROR", null, "Error interno al registrar el aviso de contenedor lleno"))
            ));
        }
    }

    public List<NewContainerSchedulerDto> createPrediction(Container container){
        DayOfWeek currentDay = LocalDateTime.now().getDayOfWeek();
        Month currentMonth = LocalDateTime.now().getMonth();
        List<ContainerFillCycle> historicalData = containerFillCycleRepository.getAllFillCycleData(
                container, currentDay, currentMonth);
        if(historicalData.isEmpty() || historicalData.size() < 5) return Collections.emptyList();;
        Map<Integer, Double> averageFillTimeByNumber = historicalData.stream()
                .collect(Collectors.groupingBy(
                        ContainerFillCycle::getDayFillingNumber,
                        Collectors.averagingDouble(data -> {
                            LocalDateTime fillTime = data.getTimeFillingNotice();
                            return fillTime.getHour() + (fillTime.getMinute() / 60.0);
                        })
                ));
        return averageFillTimeByNumber.entrySet().stream()
                .map(newPrediction -> {
                    double totalHours = newPrediction.getValue();
                    long hours = (long) Math.floor(totalHours);
                    long minutes = (long) Math.round((totalHours - hours) * 60.0);
                    if (minutes >= 60) {
                        hours += 1;
                        minutes = 0;
                    }
                    LocalDateTime predictedTime = LocalDateTime.now().toLocalDate().atStartOfDay()
                            .plusHours(hours)
                            .plusMinutes(minutes);
                    return NewContainerSchedulerDto.builder()
                            .container(container)
                            .fillingNumber(newPrediction.getKey())
                            .schedulerFillTime(predictedTime)
                            .build();
                })
                .sorted(Comparator.comparingInt(NewContainerSchedulerDto::getFillingNumber))
                .toList();
    }

    @Transactional(readOnly = true)
    public ApiResponse<AverageTimeResponseDto> completeAverageTime(){

        List<ContainerRecollectTimeProyection> granularData = containerFillCycleRepository.getAllRecollectTimeDatas();

        Double globalAverage = granularData.stream()
            .collect(Collectors.averagingDouble(ContainerRecollectTimeProyection::getAverageTime));

         List<ContainerAverageTime> containerAverageList = granularData.stream()
            .collect(Collectors.groupingBy(ContainerRecollectTimeProyection::getContainer))
            .entrySet().stream()
            .map(entry -> {
                Container container = entry.getKey();
                List<ContainerRecollectTimeProyection> containerData = entry.getValue();

                // a. Promedio Total del Contenedor
                Double totalAverage = containerData.stream()
                    .collect(Collectors.averagingDouble(ContainerRecollectTimeProyection::getAverageTime));

                // b. Promedios por Día de la Semana
                Map<DayOfWeek, Double> dayAverage = containerData.stream()
                    .collect(Collectors.groupingBy(
                        ContainerRecollectTimeProyection::getDayOfWeek,
                        Collectors.averagingDouble(ContainerRecollectTimeProyection::getAverageTime)
                    ));

                // c. Promedios por Mes
                Map<Month, Double> monthAverage = containerData.stream()
                    .collect(Collectors.groupingBy(
                        ContainerRecollectTimeProyection::getMonth,
                        Collectors.averagingDouble(ContainerRecollectTimeProyection::getAverageTime)
                    ));

                // d. Promedios por Mes y Día (Mapeo directo)
                Map<Map<Month, DayOfWeek>, Double> monthDayAverage = containerData.stream()
                    .collect(Collectors.toMap(
                        data -> Map.of(data.getMonth(), data.getDayOfWeek()),
                       ContainerRecollectTimeProyection::getAverageTime
                    ));

                return ContainerAverageTime.builder()
                    .container(container)
                    .totalAverage(totalAverage)
                    .dayAverage(dayAverage)
                    .monthAverage(monthAverage)
                    .monthDayAverage(monthDayAverage)
                    .build();
            })
            .collect(Collectors.toList());

        return new ApiResponse<>(
            errorFactory.buildMeta(HttpStatus.OK, "Promedio de tiempo de recogida completo obtenido"),
            AverageTimeResponseDto.builder()
            .globalAverage(globalAverage)
            .containerAverage(containerAverageList)
            .build(), 
            null);

    }

}
