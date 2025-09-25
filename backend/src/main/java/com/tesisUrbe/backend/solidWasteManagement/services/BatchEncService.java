package com.tesisUrbe.backend.solidWasteManagement.services;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.entities.account.User;
import com.tesisUrbe.backend.entities.solidWaste.BatchEnc;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchEncRequestDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchEncResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchRegResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.enums.BatchStatus;
import com.tesisUrbe.backend.solidWasteManagement.repository.BatchEncRepository;
import com.tesisUrbe.backend.usersManagement.services.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BatchEncService {

    private final BatchEncRepository batchRepository;
    private final BatchRegService batchRegService;
    private final UserService userService;
    private final ApiErrorFactory errorFactory;

    @Transactional
    public ApiResponse<Void> registerBatch(BatchEncRequestDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado")));
        }

        String username = auth.getName();
        Optional<User> userOpt = userService.findByUserName(username);
        if (userOpt.isEmpty()) {
            return errorFactory.build(HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("USER_NOT_FOUND", null, "Usuario autenticado no válido")));
        }

        BatchEnc batchEnc = new BatchEnc();
        batchEnc.setCreationDate(LocalDateTime.now());
        batchEnc.setDescription(dto.getDescription());
        batchEnc.setTotalWeight(BigDecimal.ZERO);
        batchEnc.setStatus(BatchStatus.IN_PROGRESS);
        batchEnc.setCreatedBy(userOpt.get());
        batchEnc.setDeleted(false);

        batchRepository.save(batchEnc);
        return errorFactory.buildSuccess(HttpStatus.CREATED, "Lote registrado exitosamente");
    }

    @Transactional
    public ApiResponse<Void> processBatch(Long batchId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado")));
        }

        String username = auth.getName();
        Optional<User> userOpt = userService.findByUserName(username);
        if (userOpt.isEmpty()) {
            return errorFactory.build(HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("USER_NOT_FOUND", null, "Usuario autenticado no válido")));
        }

        Optional<BatchEnc> batchOpt = batchRepository.findById(batchId);
        if (batchOpt.isEmpty() || batchOpt.get().isDeleted()) {
            return errorFactory.build(HttpStatus.NOT_FOUND,
                    List.of(new ApiError("BATCH_NOT_FOUND", "batchId", "Lote no encontrado o eliminado")));
        }

        BatchEnc batch = batchOpt.get();

        if (batch.getStatus() != BatchStatus.IN_PROGRESS) {
            return errorFactory.build(HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("BATCH_ALREADY_PROCESSED", "batchId", "El lote ya fue procesado o no está en progreso")));
        }

        if (batch.getDetails() == null || batch.getDetails().isEmpty()) {
            return errorFactory.build(HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("EMPTY_BATCH", "batchId", "No se puede procesar un lote sin registros de residuos")));
        }

        batch.setStatus(BatchStatus.PROCESSED);
        batch.setProcessedAt(LocalDateTime.now());
        batch.setProcessedBy(userOpt.get());

        batchRepository.save(batch);
        return errorFactory.buildSuccess(HttpStatus.OK, "Lote procesado exitosamente");
    }

    @Transactional
    public void save(BatchEnc batch) {
        batchRepository.save(batch);
    }

    @Transactional(readOnly = true)
    public Optional<BatchEnc> findById(Long id) {
        return batchRepository.findByIdAndDeletedFalse(id);
    }

    @Transactional(readOnly = true)
    public ApiResponse<BatchEncResponseDto> getBatchById(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado")));
        }

        Optional<BatchEnc> batchOpt = batchRepository.findByIdAndDeletedFalse(id);
        if (batchOpt.isEmpty()) {
            return errorFactory.build(HttpStatus.NOT_FOUND,
                    List.of(new ApiError("BATCH_NOT_FOUND", null, "Lote no encontrado")));
        }

        BatchEnc batch = batchOpt.get();

        ApiResponse<List<BatchRegResponseDto>> regResponse = batchRegService.getDetailsForBatch(batch.getId());
        List<BatchRegResponseDto> regDtos = regResponse.data();

        BatchEncResponseDto dto = BatchEncResponseDto.builder()
                .id(batch.getId())
                .creationDate(batch.getCreationDate())
                .description(batch.getDescription())
                .totalWeight(batch.getTotalWeight())
                .status(batch.getStatus())
                .processedAt(batch.getProcessedAt())
                .createdByUsername(batch.getCreatedBy().getUserName())
                .processedByUsername(batch.getProcessedBy() != null ? batch.getProcessedBy().getUserName() : null)
                .processedAt(batch.getProcessedAt())
                .details(regDtos)
                .build();

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Lote obtenido correctamente"),
                dto,
                null
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<Page<BatchEncResponseDto>> searchBatchEncAdvanced(
            String description,
            String status,
            String fechaInicio,
            String fechaFin,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado")));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir != null ? sortDir : "DESC"),
                sortBy != null ? sortBy : "creationDate");
        Pageable pageable = PageRequest.of(page, size, sort);

        LocalDate fechaInicioDate = null;
        LocalDate fechaFinDate = null;

        try {
            if (StringUtils.hasText(fechaInicio)) {
                fechaInicioDate = LocalDate.parse(fechaInicio, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            if (StringUtils.hasText(fechaFin)) {
                fechaFinDate = LocalDate.parse(fechaFin, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        } catch (Exception ignored) {}

        BatchStatus batchStatus = null;
        if (StringUtils.hasText(status)) {
            try {
                batchStatus = BatchStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        Page<BatchEnc> batchPage = batchRepository.findByAdvancedSearch(
                description, batchStatus, fechaInicioDate, fechaFinDate, pageable);

        Page<BatchEncResponseDto> dtoPage = batchPage.map(batch -> BatchEncResponseDto.builder()
                .id(batch.getId())
                .creationDate(batch.getCreationDate())
                .description(batch.getDescription())
                .totalWeight(batch.getTotalWeight())
                .status(batch.getStatus())
                .processedAt(batch.getProcessedAt())
                .createdByUsername(batch.getCreatedBy().getUserName())
                .processedByUsername(batch.getProcessedBy() != null ? batch.getProcessedBy().getUserName() : null)
                .processedAt(batch.getProcessedAt())
                .build());

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Búsqueda avanzada de lotes realizada exitosamente"),
                dtoPage,
                null
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<Page<BatchEncResponseDto>> getAllBatches(int page, int size, String sortBy, String sortDir) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado")));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir != null ? sortDir : "DESC"),
                sortBy != null ? sortBy : "creationDate");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<BatchEnc> batchPage = batchRepository.findByDeletedFalse(pageable);

        Page<BatchEncResponseDto> dtoPage = batchPage.map(batch -> BatchEncResponseDto.builder()
                .id(batch.getId())
                .creationDate(batch.getCreationDate())
                .description(batch.getDescription())
                .totalWeight(batch.getTotalWeight())
                .status(batch.getStatus())
                .processedAt(batch.getProcessedAt())
                .createdByUsername(batch.getCreatedBy().getUserName())
                .processedByUsername(batch.getProcessedBy() != null ? batch.getProcessedBy().getUserName() : null)
                .processedAt(batch.getProcessedAt())
                .build());

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Lotes obtenidos correctamente"),
                dtoPage,
                null
        );
    }

    @Transactional
    public ApiResponse<Void> softDeleteBatch(Long id) {
        Optional<BatchEnc> batchOpt = batchRepository.findByIdAndDeletedFalse(id);
        if (batchOpt.isEmpty()) {
            return errorFactory.build(HttpStatus.NOT_FOUND,
                    List.of(new ApiError("BATCH_NOT_FOUND", null, "Lote no encontrado")));
        }

        BatchEnc batch = batchOpt.get();
        batch.setDeleted(true);
        batchRepository.save(batch);

        return errorFactory.buildSuccess(HttpStatus.OK, "Lote eliminado correctamente");
    }
}
