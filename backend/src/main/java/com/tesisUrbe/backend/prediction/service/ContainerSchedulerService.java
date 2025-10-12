package com.tesisUrbe.backend.prediction.service;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.prediction.dto.ManualSchedulerDto;
import com.tesisUrbe.backend.prediction.dto.NewContainerSchedulerDto;
import com.tesisUrbe.backend.prediction.model.ContainerScheduler;
import com.tesisUrbe.backend.prediction.repository.ContainerSchedulerRepository;
import com.tesisUrbe.backend.solidWasteManagement.enums.ContainerStatus;
import com.tesisUrbe.backend.solidWasteManagement.repository.ContainerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContainerSchedulerService {

    private final ContainerFillCycleService containerFillCycleService;
    private final ContainerSchedulerRepository containerSchedulerRepository;
    private final ContainerRepository containerRepository;
    private final ApiErrorFactory errorFactory;

    @Transactional
    public ApiResponse<List<List<ContainerScheduler>>> schedulerPredictions(List<String> containerSerial) {

        List<List<ContainerScheduler>> allContainerSchedulers = new ArrayList<>(List.of());
        List<ApiError> errors = new ArrayList<>(List.of());

        for(String serial : containerSerial){
            Optional<Container> container = containerRepository.findBySerialAndDeletedFalse(serial);

            if(container.isEmpty()){
                errors.add(new ApiError("CONTAINER_NOT_FOUND", null,
                        "Contenedor con el serial " + serial + " no encontrado"));
                continue;
            }

            Container verContainer = container.get();

            List<NewContainerSchedulerDto> containerPredictions = containerFillCycleService.createPrediction(verContainer);

            if(containerPredictions.isEmpty()){
                errors.add(new ApiError("INSUFFICIENT_DATA", null,
                        "Contenedor con el serial " + serial + " no posee data para generar predicciones"));
                continue;
            }

            List<ContainerScheduler> containerSchedulers = containerPredictions.stream()
                    .map(prediction -> ContainerScheduler.builder()
                            .container(prediction.getContainer())
                            .schedulerFillTime(prediction.getSchedulerFillTime())
                            .build())
                    .toList();

            allContainerSchedulers.add(containerSchedulerRepository.saveAll(containerSchedulers));
        }

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Prediccion de Cronograma completo"),
                allContainerSchedulers,
                errors
        );

    }

    @Transactional
    public ApiResponse<List<List<ContainerScheduler>>>  schedulerManual(List<ManualSchedulerDto> scheduler){
        List<List<ContainerScheduler>> allContainerSchedulers = new ArrayList<>(List.of());
        List<ApiError> errors = new ArrayList<>(List.of());

        for(ManualSchedulerDto containerScheduler : scheduler){
            Optional<Container> container = containerRepository.findBySerialAndDeletedFalse(containerScheduler.getContainerSerial());

            if(container.isEmpty()){
                errors.add(new ApiError("CONTAINER_NOT_FOUND", null,
                        "Contenedor con el serial " + containerScheduler.getContainerSerial() + " no encontrado"));
                continue;
            }

            Container verContainer = container.get();

            List<ContainerScheduler> containerSchedulers = containerScheduler.getSchedulers().stream()
                    .map(prediction -> ContainerScheduler.builder()
                            .container(verContainer)
                            .schedulerFillTime(prediction)
                            .build())
                    .toList();

            allContainerSchedulers.add(containerSchedulerRepository.saveAll(containerSchedulers));
        }

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Prediccion de Cronograma completo"),
                allContainerSchedulers,
                errors
        );
    }

    @Transactional
    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.MINUTES) // Ejecuta cada 15 minutos
    public void checkOverdueSchedules() {

        LocalDateTime now = LocalDateTime.now();

        List<ContainerScheduler> overdueSchedules =
                containerSchedulerRepository.findAllByWasUsedFalseAndWasSuspendedFalseAndSchedulerFillTimeBefore(now);

        if (overdueSchedules.isEmpty()) {
            System.out.println(now + " - Tarea programada: No se encontraron cronogramas vencidos.");
            return;
        }

        for (ContainerScheduler scheduler : overdueSchedules) {
            Container container = scheduler.getContainer();
            container.setStatus(ContainerStatus.FULL);
            containerRepository.save(container);

            containerFillCycleService.fillContainerNotice(container);

            scheduler.setWasUsed(true);
            containerSchedulerRepository.save(scheduler);
        }
    }

}
