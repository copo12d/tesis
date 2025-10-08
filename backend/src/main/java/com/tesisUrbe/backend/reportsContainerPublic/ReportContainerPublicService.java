package com.tesisUrbe.backend.reportsContainerPublic;

import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.solidWasteManagement.repository.ContainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportContainerPublicService {

    private final ReportContainerPublicRepository repository;
    private final ContainerRepository containerRepository;

    @Transactional
    public ReportContainerPublic create(ReportContainerPublicDto dto) {
        log.info("Iniciando creación de reporte público para contenedor ID: {}", dto.getContainerId());
        log.debug("DTO recibido: {}", dto);

        Container container = containerRepository.findById(dto.getContainerId())
                .orElseThrow(() -> {
                    log.warn("Contenedor no encontrado con ID: {}", dto.getContainerId());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Container not found");
                });

        ReportContainerPublic report = ReportContainerPublic.builder()
                .container(container)
                .previousStatus(dto.getPreviousStatus())
                .newStatus(dto.getNewStatus())
                .fillLevel(dto.getFillLevel())
                .message(dto.getMessage())
                .notified(false)
                .validForPrediction(false)
                .escalatedToCycle(false)
                .build();

        log.debug("Entidad construida para persistencia: {}", report);

        try {
            ReportContainerPublic saved = repository.save(report);
            log.info("Reporte guardado exitosamente con ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("Error al guardar el reporte público del contenedor", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo guardar el reporte");
        }
    }

    @Transactional
    public ReportContainerPublic create(ReportContainerPublic report) {
        log.info("Creando reporte público desde entidad directa");
        log.debug("Entidad recibida: {}", report);

        if (!report.isValidForPrediction()) {
            report.setValidForPrediction(false);
        }
        if (!report.isEscalatedToCycle()) {
            report.setEscalatedToCycle(false);
        }

        try {
            ReportContainerPublic saved = repository.save(report);
            log.info("Reporte guardado exitosamente con ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("Error al guardar el reporte público del contenedor", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo guardar el reporte");
        }
    }

    @Transactional(readOnly = true)
    public List<ReportContainerPublic> getUnnotifiedReports() {
        log.info("Consultando reportes públicos no notificados...");
        List<ReportContainerPublic> reports = repository.findByNotifiedFalse();
        log.debug("Reportes encontrados: {}", reports.size());
        return reports;
    }

    @Transactional
    public void markNotified(Long id) {
        log.info("Marcando reporte como notificado. ID: {}", id);
        repository.findById(id).ifPresentOrElse(r -> {
            r.setNotified(true);
            repository.save(r);
            log.info("Reporte ID {} marcado como notificado", id);
        }, () -> {
            log.warn("No se encontró reporte con ID: {}", id);
        });
    }
}
