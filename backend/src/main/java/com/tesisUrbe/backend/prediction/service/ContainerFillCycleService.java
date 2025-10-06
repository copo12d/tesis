package com.tesisUrbe.backend.prediction.service;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.prediction.dto.NewContainerSchedulerDto;
import com.tesisUrbe.backend.prediction.model.ContainerFillCycleData;
import com.tesisUrbe.backend.prediction.model.ContainerScheduler;
import com.tesisUrbe.backend.prediction.repository.ContainerFillCycleDataRepository;
import com.tesisUrbe.backend.solidWasteManagement.enums.ContainerStatus;
import com.tesisUrbe.backend.solidWasteManagement.repository.ContainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Transactional
    public Optional<ApiResponse<Void>> fillContainerNotice(Container container){

        Optional<ContainerFillCycleData> lastNotice = containerFillCycleDataRepository
                .findTop1ByContainerAndDeletedFalseOrderByTimeFillingNoticeDesc(container);

        int fillingNumber;
        double hoursBetweenFilling;

        if(lastNotice.isEmpty()){
            fillingNumber = 1;
            hoursBetweenFilling = 0;
        } else {
            ContainerFillCycleData verifiedLastNotice = lastNotice.get();
            if(verifiedLastNotice.getMinutesToEmpty() == null){
                return Optional.of(errorFactory.build(
                        HttpStatus.PROCESSING,
                        List.of(new ApiError("FILLING_CYCLE_INCOMPLETE", null, "Contenedor sin reporte de tiempo de vaciado"))
                ));
            }

            if(LocalDate.now().equals(verifiedLastNotice.getTimeFillingNotice().toLocalDate())){
                fillingNumber = verifiedLastNotice.getDayFillingNumber() + 1;
            } else {
                fillingNumber = 1;
            }
            long minutesDifference = ChronoUnit.MINUTES.between(
                    verifiedLastNotice.getTimeFillingNotice().plusMinutes(verifiedLastNotice.getMinutesToEmpty()),
                    LocalDateTime.now());

            hoursBetweenFilling = (double) minutesDifference/60.0;
        }

        ContainerFillCycleData newNotice = ContainerFillCycleData.builder()
            .container(container)
            .dayFillingNumber(fillingNumber)
            .hoursBetweenFilling(hoursBetweenFilling)
            .build();

        try {
            containerFillCycleDataRepository.save(newNotice);
            return Optional.empty();
        } catch (Exception e) {
             return Optional.of(errorFactory.build(
                     HttpStatus.INTERNAL_SERVER_ERROR,
                     List.of(new ApiError("PERSISTENCE_ERROR", null, "Error interno al registrar el aviso de contenedor lleno"))
             ));
        }
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
                        // Llave: Número de llenado en el día (1, 2, 3...)
                        ContainerFillCycleData::getDayFillingNumber,
                        // Valor: Promedio de la hora absoluta del llenado
                        Collectors.averagingDouble(data -> {
                            LocalDateTime fillTime = data.getTimeFillingNotice();
                            // Horas desde medianoche: 0.00 a 23.99
                            return fillTime.getHour() + (fillTime.getMinute() / 60.0);
                        })
                ));

        return averageFillTimeByNumber.entrySet().stream()
                .map(newPrediction -> {
                    double totalHours = newPrediction.getValue();

                    // Separar la parte entera (horas) y la parte decimal (minutos)
                    long hours = (long) Math.floor(totalHours);
                    // Calcular los minutos a partir del remanente decimal
                    long minutes = (long) Math.round((totalHours - hours) * 60.0);

                    // Ajuste si los minutos redondean a 60 (ej: 2.9999 horas -> 3 horas 0 minutos)
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

}
