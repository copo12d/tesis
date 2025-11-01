package com.tesisUrbe.backend.solidWasteManagement.services;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.entities.solidWaste.BatchReg;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchRegResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.repository.BatchRegRepository;
import com.tesisUrbe.backend.solidWasteManagement.repository.ContainerTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BatchRegService {

    private final BatchEncService batchEncService;
    private final BatchRegRepository batchRegRepository;
    private final ApiErrorFactory errorFactory;
    private final ContainerTypeRepository containerTypeRepository;

    @Transactional(readOnly = true)
    public ApiResponse<List<BatchRegResponseDto>> getAllBatchRegs() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado")));
        }

        List<BatchReg> regs = batchRegRepository.findByDeletedFalse();

        List<BatchRegResponseDto> dtos = regs.stream()
                .map(this::toDto)
                .toList();

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Registros obtenidos correctamente"),
                dtos,
                null
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<Page<BatchRegResponseDto>> getByBatchEncWithFilters(
            Long batchEncId,
            String serial,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado")));
        }

        if (!batchEncService.batchExists(batchEncId)) {
            return errorFactory.build(HttpStatus.NOT_FOUND,
                    List.of(new ApiError("BATCH_NOT_FOUND", null, "El lote especificado no existe")));
        }

        List<BatchReg> base = batchRegRepository.findByBatchEncIdAndDeletedFalse(batchEncId);

        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(23, 59, 59) : null;

        List<BatchRegResponseDto> filteredDtos = base.stream()
                .filter(reg -> serial == null || reg.getContainer().getSerial().equalsIgnoreCase(serial))
                .filter(reg -> start == null || !reg.getCollectionDate().isBefore(start))
                .filter(reg -> end == null || !reg.getCollectionDate().isAfter(end))
                .map(this::toDto)
                .toList();

        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), filteredDtos.size());
        List<BatchRegResponseDto> pageContent = filteredDtos.subList(startIndex, endIndex);

        Page<BatchRegResponseDto> page = new PageImpl<>(pageContent, pageable, filteredDtos.size());

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Registros filtrados correctamente"),
                page,
                null
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<Map<String, Object>>> getDailyContainerSummary() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado")));
        }

        List<BatchReg> registros = batchRegRepository.findByDeletedFalse();

        List<String> tipos = containerTypeRepository.findAllActive()
                .stream()
                .map(ct -> ct.getName().toLowerCase())
                .toList();

        Set<String> tiposActivos = new HashSet<>(tipos);

        LocalDate hoy = LocalDate.now();
        DayOfWeek primerDiaSemana = DayOfWeek.MONDAY;
        LocalDate inicioSemana = hoy.with(TemporalAdjusters.previousOrSame(primerDiaSemana));
        LocalDate finSemana = inicioSemana.plusDays(6);

        Map<DayOfWeek, Map<String, Integer>> agrupado = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek dia : DayOfWeek.values()) {
            Map<String, Integer> materiales = new HashMap<>();
            for (String tipo : tipos) {
                materiales.put(tipo, 0);
            }
            agrupado.put(dia, materiales);
        }

        for (BatchReg reg : registros) {
            LocalDate fecha = reg.getCollectionDate().toLocalDate();
            if (fecha.isBefore(inicioSemana) || fecha.isAfter(finSemana)) continue;

            String tipo = reg.getContainer().getContainerType().getName().toLowerCase();
            if (!tiposActivos.contains(tipo)) continue;

            DayOfWeek dia = fecha.getDayOfWeek();
            Map<String, Integer> materiales = agrupado.get(dia);
            materiales.put(tipo, materiales.get(tipo) + 1);
        }

        List<Map<String, Object>> resultado = Arrays.stream(DayOfWeek.values())
                .map(dia -> {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    fila.put("day", dia.getDisplayName(TextStyle.FULL, new Locale("es")));
                    Map<String, Integer> materiales = agrupado.get(dia);
                    materiales.forEach(fila::put);
                    return fila;
                })
                .toList();

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Resumen diario de contenedores generado correctamente"),
                resultado,
                null
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<Map<String, Object>>> getWeeklyContainerSummary() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado")));
        }

        List<BatchReg> registros = batchRegRepository.findByDeletedFalse();

        List<String> tipos = containerTypeRepository.findAllActive()
                .stream()
                .map(ct -> ct.getName().toLowerCase())
                .toList();

        Set<String> tiposActivos = new HashSet<>(tipos);

        LocalDate hoy = LocalDate.now();
        Month mesActual = hoy.getMonth();
        int anioActual = hoy.getYear();

        Map<Integer, Map<String, Integer>> agrupado = new LinkedHashMap<>();
        for (int semana = 1; semana <= 5; semana++) {
            Map<String, Integer> materiales = new HashMap<>();
            for (String tipo : tipos) {
                materiales.put(tipo, 0);
            }
            agrupado.put(semana, materiales);
        }

        for (BatchReg reg : registros) {
            LocalDate fecha = reg.getCollectionDate().toLocalDate();
            if (!fecha.getMonth().equals(mesActual) || fecha.getYear() != anioActual) continue;

            String tipo = reg.getContainer().getContainerType().getName().toLowerCase();
            if (!tiposActivos.contains(tipo)) continue;

            int semanaDelMes = (fecha.getDayOfMonth() - 1) / 7 + 1;
            Map<String, Integer> materiales = agrupado.get(semanaDelMes);
            materiales.put(tipo, materiales.get(tipo) + 1);
        }

        List<Map<String, Object>> resultado = agrupado.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    fila.put("week", "Semana " + entry.getKey());
                    entry.getValue().forEach(fila::put);
                    return fila;
                })
                .toList();

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Resumen semanal de contenedores generado correctamente"),
                resultado,
                null
        );
    }

    @Transactional
    public void save(BatchReg batchReg) {
        batchRegRepository.save(batchReg);
    }

    private BatchRegResponseDto toDto(BatchReg reg) {
        LocalDateTime collectionDate = reg.getCollectionDate();

        return BatchRegResponseDto.builder()
                .serial(reg.getContainer().getSerial())
                .weight(reg.getWeight())
                .createdByUsername(reg.getCreatedBy().getUserName())
                .date(collectionDate.toLocalDate().toString())
                .hour(collectionDate.toLocalTime().toString())
                .build();
    }

}
