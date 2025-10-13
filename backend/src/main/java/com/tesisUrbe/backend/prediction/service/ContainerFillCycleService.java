package com.tesisUrbe.backend.prediction.service;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.prediction.dto.AverageTimeResponseDto;
import com.tesisUrbe.backend.prediction.dto.ContainerAverageTime;
import com.tesisUrbe.backend.prediction.dto.ContainerRecollectTimeProyection;
import com.tesisUrbe.backend.prediction.dto.NewContainerSchedulerDto;
import com.tesisUrbe.backend.prediction.model.ContainerFillCycleData;
import com.tesisUrbe.backend.prediction.repository.ContainerFillCycleDataRepository;
import com.tesisUrbe.backend.solidWasteManagement.enums.ContainerStatus;
import com.tesisUrbe.backend.solidWasteManagement.repository.ContainerRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContainerFillCycleService {
    private final ContainerFillCycleDataRepository containerFillCycleDataRepository;
    private final ApiErrorFactory errorFactory;
    private final ContainerRepository containerRepository;

    @Transactional
    public Optional<ApiResponse<Void>> reportContainerBySerial(String serial, HttpServletRequest request) {
        Optional<Container> containerOpt = containerRepository.findBySerialAndDeletedFalse(serial);

        if (containerOpt.isEmpty()) {
            return Optional.of(new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.NOT_FOUND, "Contenedor no encontrado"),
                    null,
                    List.of(new ApiError("CONTAINER_NOT_FOUND", "serial",
                            "Contenedor con el serial " + serial + " no fue encontrado"))
            ));
        }
        return fillContainerNotice(containerOpt.get(), request);
    }

    @Transactional
    public Optional<ApiResponse<Void>> fillContainerNotice(Container container, HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        boolean alreadyReported = containerFillCycleDataRepository
                .existsByContainerAndReporterIpAndTimeFillingNoticeAfter(
                        container, ip, LocalDateTime.now().minusHours(1));
        if (alreadyReported) {
            return Optional.of(errorFactory.build(
                    HttpStatus.TOO_MANY_REQUESTS,
                    List.of(new ApiError("DUPLICATE_REPORT", null,
                            "Ya has reportado este contenedor en la última hora"))
            ));
        }
        long distinctReporters = containerFillCycleDataRepository
                .countDistinctReporterIpByContainerAndTimeFillingNoticeAfter(
                        container, LocalDateTime.now().minusHours(1));
        boolean shouldMarkAsFull = distinctReporters + 1 >= 3; // CANTIDAD DE REPORTES PARA VALIDAR ESTADO FULL
        Optional<ContainerFillCycleData> lastNotice = containerFillCycleDataRepository
                .findTop1ByContainerAndDeletedFalseOrderByTimeFillingNoticeDesc(container);
        int fillingNumber;
        double hoursBetweenFilling;
        if (lastNotice.isEmpty()) {
            fillingNumber = 1;
            hoursBetweenFilling = 0;
        } else {
            ContainerFillCycleData verifiedLastNotice = lastNotice.get();
            if (verifiedLastNotice.getMinutesToEmpty() == null) {
                return Optional.of(errorFactory.build(
                        HttpStatus.PROCESSING,
                        List.of(new ApiError("FILLING_CYCLE_INCOMPLETE", null,
                                "Contenedor sin reporte de tiempo de vaciado"))
                ));
            }
            if (LocalDate.now().equals(verifiedLastNotice.getTimeFillingNotice().toLocalDate())) {
                fillingNumber = verifiedLastNotice.getDayFillingNumber() + 1;
            } else {
                fillingNumber = 1;
            }
            long minutesDifference = ChronoUnit.MINUTES.between(
                    verifiedLastNotice.getTimeFillingNotice().plusMinutes(verifiedLastNotice.getMinutesToEmpty()),
                    LocalDateTime.now());
            hoursBetweenFilling = (double) minutesDifference / 60.0;
        }
        ContainerFillCycleData newNotice = ContainerFillCycleData.builder()
                .container(container)
                .dayFillingNumber(fillingNumber)
                .hoursBetweenFilling(hoursBetweenFilling)
                .reporterIp(ip)
                .build();
        try {
            containerFillCycleDataRepository.save(newNotice);
            if (shouldMarkAsFull) {
                container.setStatus(ContainerStatus.FULL);
                containerRepository.save(container);
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(errorFactory.build(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    List.of(new ApiError("PERSISTENCE_ERROR", null,
                            "Error interno al registrar el aviso de contenedor lleno"))
            ));
        }
    }

    @Transactional
    public Optional<ApiResponse<Void>> fillContainerNotice(Container container) {
        return fillContainerNotice(container, null);
    }

    @Transactional
    public Optional<ApiResponse<Void>> cancelContainerNotice(Container container){
        Optional<ContainerFillCycleData> lastNotice = containerFillCycleDataRepository
                .findTop1ByContainerAndDeletedFalseOrderByTimeFillingNoticeDesc(container);
        if(lastNotice.isEmpty())
            return Optional.of(errorFactory.build(
                HttpStatus.NOT_FOUND,
                List.of(new ApiError("FILL_NOTICE_NOT_FOUND", null, "No se ha encontrado ninguna notificacion para cancelar"))
            ));
        ContainerFillCycleData fillCycleData = lastNotice.get();
        if(fillCycleData.getMinutesToEmpty() != null)
            return Optional.of(errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("FILLING_CYCLE_COMPLETE", null, "Ya se recogio el contenedor")))
            );
        fillCycleData.setDeleted(true);
        try {
            containerFillCycleDataRepository.save(fillCycleData);
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(errorFactory.build(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    List.of(new ApiError("PERSISTENCE_ERROR", null, "Error interno al cancelar la notificacion")))
            );
        }
    }

    @Transactional
    public Optional<ApiResponse<Void>> completeFillingCycle(Container container){
        Optional<ContainerFillCycleData> lastNotice = containerFillCycleDataRepository
                .findTop1ByContainerAndDeletedFalseOrderByTimeFillingNoticeDesc(container);
        if(lastNotice.isEmpty()) return Optional.of(
                errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("FILL_NOTICE_NOT_FOUND", null, "No se ha encontrado ninguna notificacion pendiente")))
        );
        ContainerFillCycleData fillCycleData = lastNotice.get();
        if(fillCycleData.getMinutesToEmpty() != null)
            return Optional.of(errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("FILLING_CYCLE_COMPLETE", null, "Ya se recogio el contenedor")))
            );
        long minutesToEmpty = ChronoUnit.MINUTES.between(fillCycleData.getTimeFillingNotice(), LocalDateTime.now());
        fillCycleData.setMinutesToEmpty(minutesToEmpty);
        try {
            containerFillCycleDataRepository.save(fillCycleData);
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
        List<ContainerFillCycleData> historicalData = containerFillCycleDataRepository.getAllFillCycleData(
                container, currentDay, currentMonth);
        if(historicalData.isEmpty() || historicalData.size() < 5) return Collections.emptyList();;
        Map<Integer, Double> averageFillTimeByNumber = historicalData.stream()
                .collect(Collectors.groupingBy(
                        ContainerFillCycleData::getDayFillingNumber,
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

        List<ContainerRecollectTimeProyection> granularData = containerFillCycleDataRepository.getAllRecollectTimeDatas();

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
