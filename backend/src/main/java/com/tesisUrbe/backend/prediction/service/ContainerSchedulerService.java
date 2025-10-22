package com.tesisUrbe.backend.prediction.service;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.prediction.dto.*;
import com.tesisUrbe.backend.prediction.model.ContainerScheduler;
import com.tesisUrbe.backend.prediction.repository.ContainerSchedulerRepository;
import com.tesisUrbe.backend.solidWasteManagement.repository.ContainerRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ContainerSchedulerService {

    private final ContainerFillCycleService containerFillCycleService;
    private final ContainerSchedulerRepository containerSchedulerRepository;
    private final ContainerRepository containerRepository;
    private final ApiErrorFactory errorFactory;

    /**
     * Genera y persiste predicciones de horarios de llenado (recolección) para una lista
     * de contenedores basada en su historial de ciclos de llenado.
     * <p>
     * Este método es transaccional: intenta procesar cada contenedor de la lista. Si se
     * encuentran errores (como contenedor no encontrado o datos históricos insuficientes),
     * estos se acumulan en la respuesta, pero el procesamiento continúa para los demás contenedores.
     * Las predicciones exitosas se guardan en la base de datos.
     * </p>
     *
     * @param containerSerial Una lista de números de serie (String) de los contenedores a procesar.
     * @return Un objeto {@code ApiResponse} que encapsula el resultado. El cuerpo de la respuesta
     * contendrá una lista de listas de {@code ContainerScheduler}, donde cada lista interna
     * representa los nuevos cronogramas guardados para un contenedor específico.
     * La lista de errores contendrá detalles sobre los contenedores que no pudieron ser procesados.
     */
    @Transactional
    public ApiResponse<List<List<ContainerScheduler>>> schedulerPredictions(List<String> containerSerial) {

        List<List<ContainerScheduler>> allContainerSchedulers = new ArrayList<>(List.of());
        List<ApiError> errors = new ArrayList<>(List.of());

        for (String serial : containerSerial) {
            Optional<Container> container = containerRepository.findBySerialAndDeletedFalse(serial);

            if (container.isEmpty()) {
                errors.add(new ApiError("CONTAINER_NOT_FOUND", null,
                        "Contenedor con el serial " + serial + " no encontrado"));
                continue;
            }

            Container verContainer = container.get();

            List<NewContainerSchedulerDto> containerPredictions = containerFillCycleService.createPrediction(verContainer);

            if (containerPredictions.isEmpty()) {
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
     * Crea y persiste un nuevo conjunto de cronogramas de recolección para un contenedor
     * específico de manera manual.
     * <p>
     * El método toma un {@code ManualSchedulerDto} que contiene el serial del contenedor
     * y una lista de fechas y horas programadas. Primero valida la existencia del contenedor.
     * Si es exitoso, mapea las horas programadas a entidades {@code ContainerScheduler} y las guarda.
     * </p>
     *
     * @param scheduler Objeto {@code ManualSchedulerDto} que contiene el serial del contenedor
     * y la lista de {@code LocalDateTime} para los horarios de recolección.
     * @return Un objeto {@code ApiResponse} que encapsula el resultado de la operación.
     * <ul>
     * <li>Si es exitoso (HTTP 200 OK), devuelve una lista de {@code NextRecollectionDto} con
     * los cronogramas creados.</li>
     * <li>Si el contenedor especificado no es válido o no se encuentra, devuelve un error
     * {@code BAD_REQUEST} con el código {@code INVALID_CONTAINER}.</li>
     * </ul>
     */
    @Transactional
    public ApiResponse<List<NextRecollectionDto>> schedulerManual(ManualSchedulerDto scheduler) {

        Optional<Container> container = containerRepository.findBySerialAndDeletedFalse(scheduler.getContainerSerial());

        if (container.isEmpty())
            return errorFactory.build(HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("INVALID_CONTAINER", "containerId", "Contenedor no válido")));

        Container verContainer = container.get();

        List<ContainerScheduler> containerSchedulers = scheduler.getSchedulers().stream()
                .map(prediction -> ContainerScheduler.builder()
                        .container(verContainer)
                        .schedulerFillTime(prediction)
                        .build())
                .toList();

        List<ContainerScheduler> savedScheduler = containerSchedulerRepository.saveAll(containerSchedulers);

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Cronograma del contenedor completo"),
                savedScheduler.stream()
                        .map(singleScheduler -> NextRecollectionDto.builder()
                                .containerSerial(singleScheduler.getContainer().getSerial())
                                .nextRecollectionTime(singleScheduler.getSchedulerFillTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
                                .build()
                        )
                        .toList(),
                null
        );
    }

    /**
     * Realiza una búsqueda paginada y opcionalmente filtrada de la próxima recolección programada
     * para todos los contenedores activos.
     * <p>
     * Este método consulta una proyección que incluye el serial del contenedor y su próxima hora
     * de recolección programada. Los resultados se mapean a {@code NextRecollectionDto} para la respuesta.
     * </p>
     *
     * @param serial Filtro opcional por el número de serie del contenedor. Si se proporciona,
     * se utiliza un patrón 'like' (búsqueda parcial). Si es {@code null} o vacío,
     * se omitirá el filtro.
     * @param pageable Objeto de paginación que incluye el número de página, tamaño y criterios de ordenación.
     * @return Un objeto {@code ApiResponse} que contiene una página de {@code NextRecollectionDto}.
     * Cada DTO incluye el serial del contenedor y la hora programada formateada (o un mensaje
     * indicando que no hay programación). El estado HTTP será 200 OK.
     */
    @Transactional(readOnly = true)
    public ApiResponse<Page<NextRecollectionDto>> searchNextRecollectionPage(
            String serial, Pageable pageable
    ) {
        Page<NextRecollectionProjection> proyectionPage = containerRepository.nextRecollectionAllContainers(
                (serial == null || serial.isEmpty()) ? null : "%" + serial + "%",
                pageable);

        return new ApiResponse<Page<NextRecollectionDto>>(
                errorFactory.buildMeta(HttpStatus.OK, "Busqueda de siguientes recolecciones programadas completa"),
                new PageImpl<>(proyectionPage.getContent().stream()
                        .map(container -> NextRecollectionDto.builder()
                                .containerSerial(container.getContainerSerial())
                                .nextRecollectionTime(
                                        (container.getNextRecollectionTime() == null) ? "No tiene ninguna recolección programada"
                                                : container.getNextRecollectionTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
                                .build())
                        .toList(),
                        proyectionPage.getPageable(),
                        proyectionPage.getTotalElements()),
                null
        );
    }

    //Listo
    @Transactional(readOnly = true)
    public ApiResponse<List<NextRecollectionDto>> nextRecollectionsBySerial(String serial) {

        Optional<Container> optContainer = containerRepository.findBySerialAndDeletedFalse(serial);

        if(optContainer.isEmpty())
            return errorFactory.build(HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("INVALID_CONTAINER", "containerId", "Contenedor no válido")));

        List<SchedulerProjection> scheduler = containerSchedulerRepository.containerScheduler(
                optContainer.get(), LocalDateTime.now());

        return new ApiResponse<List<NextRecollectionDto>>(
                errorFactory.buildMeta(HttpStatus.OK, "Lista de todas las recolecciones programadas"),
                scheduler.stream()
                        .map(time -> NextRecollectionDto.builder()
                                .id(time.getId())
                                .nextRecollectionTime(time.getSchedulerFillTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
                                .build())
                        .toList(),
                null
        );
    }

    @Transactional
    public ApiResponse<Void> adminSchedulerSuspension(Long id){

        containerSchedulerRepository.suspendById(id);

        return errorFactory.buildSuccess(HttpStatus.OK, "Recoleccion programada suspendida");
    }

    /**
     * Verifica periódicamente los cronogramas de recolección de contenedores que están vencidos
     * o que deberían haberse iniciado.
     * <p>
     * Este método se ejecuta automáticamente cada 15 minutos. Por cada cronograma vencido encontrado,
     * intenta iniciar el ciclo de llenado del contenedor.
     * </p>
     */
    @Transactional
    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.MINUTES) // Ejecuta cada 15 minutos
    public void checkOverdueSchedules() {

        LocalDateTime now = LocalDateTime.now();

        List<SchedulerProjection> overdueSchedules = containerSchedulerRepository.findAllActiveScheduler(now);

        if (overdueSchedules.isEmpty()) {
            System.out.println(now + " - Tarea programada: No se encontraron cronogramas vencidos.");
            return;
        }

        for (SchedulerProjection scheduler : overdueSchedules) {
            Container container = scheduler.getContainer();

            ApiResponse<Void> response = containerFillCycleService.fillContainerNotice(container);
            if(HttpStatus.OK.value() == response.meta().status()){
                containerSchedulerRepository.setAllUsed(container, now);
            } else {
                containerSchedulerRepository.suspendForCurrentCycle(container, now);
            }
        }
    }



}
