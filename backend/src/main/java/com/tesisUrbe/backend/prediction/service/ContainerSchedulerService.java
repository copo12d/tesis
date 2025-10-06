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

/**
 * Servicio encargado de gestionar y persistir los cronogramas de llenado (schedulers) de contenedores.
 * <p>Este servicio maneja dos flujos principales: la generación automática de cronogramas
 * a partir de predicciones estadísticas y el registro manual de horarios por parte de un usuario.</p>
 *
 * @author José
 * @version 1.0
 * @since 2025-10-05
 */
@Service
@RequiredArgsConstructor
public class ContainerSchedulerService {

    private final ContainerFillCycleService containerFillCycleService;
    private final ContainerSchedulerRepository containerSchedulerRepository;
    private final ContainerRepository containerRepository;
    private final ApiErrorFactory errorFactory;

    /**
     * Genera y persiste un cronograma de llenado para una lista de contenedores
     * basándose en las predicciones estadísticas calculadas para el día de hoy.
     * <p>El proceso llama al ContainerFillCycleService para obtener los horarios predichos
     * y maneja los errores de contenedores no encontrados o con data insuficiente.</p>
     *
     * @param containerSerial Lista de seriales de los contenedores para los que se generarán predicciones.
     * @return ApiResponse con una lista de listas de ContainerScheduler (cada lista interna corresponde a un contenedor),
     * y una lista de errores si algún contenedor no pudo ser procesado.
     * @see ContainerFillCycleService#createPrediction(Container)
     */
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

    /**
     * Registra manualmente un cronograma de llenado para una lista de contenedores.
     * <p>Este método toma una lista de objetos que contienen el serial del contenedor y
     * los horarios de llenado deseados (LocalDateTime), y los persiste directamente.</p>
     *
     * @param scheduler Lista de objetos ManualSchedulerDto que contienen el serial del contenedor
     * y los horarios de llenado manuales.
     * @return ApiResponse con una lista de listas de ContainerScheduler creados y una lista
     * de errores si algún contenedor no pudo ser encontrado.
     */
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


    /**
     * Tarea programada que se ejecuta cada 15 minutos.
     * Revisa los cronogramas de llenado cuya hora de activación (schedulerFillTime)
     * ha pasado, y que no han sido usados ni suspendidos.
     * Se usa 'fixedRate' para iniciar el conteo de 15 minutos al finalizar la ejecución previa.
     */
    @Transactional
    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.MINUTES) // Ejecuta cada 15 minutos
    public void checkOverdueSchedules() {

        LocalDateTime now = LocalDateTime.now();

        // 1. Obtener la lista de cronogramas pasados y activos
        List<ContainerScheduler> overdueSchedules =
                containerSchedulerRepository.findAllByWasUsedFalseAndWasSuspendedFalseAndSchedulerFillTimeBefore(now);

        if (overdueSchedules.isEmpty()) {
            System.out.println(now + " - Tarea programada: No se encontraron cronogramas vencidos.");
            return;
        }

        for (ContainerScheduler scheduler : overdueSchedules) {
            //Guarda el contenedor como full
            Container container = scheduler.getContainer();
            container.setStatus(ContainerStatus.FULL);
            containerRepository.save(container);

            //Crea el aviso para futuras predicciones
            containerFillCycleService.fillContainerNotice(container);

            //Marca como usado el cronograma
            scheduler.setWasUsed(true);
            containerSchedulerRepository.save(scheduler);
        }
    }

}
