package com.tesisUrbe.backend.solidWasteManagement.services;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.entities.solidWaste.BatchReg;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchRegResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.repository.BatchRegRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchRegService {

    private final BatchRegRepository batchRegRepository;
    private final ApiErrorFactory errorFactory;

    @Transactional(readOnly = true)
    public ApiResponse<List<BatchRegResponseDto>> getDetailsForBatch(Long batchEncId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado")));
        }

        List<BatchReg> regs = batchRegRepository.findByBatchEncIdAndDeletedFalse(batchEncId);
        List<BatchRegResponseDto> dtos = regs.stream().map(this::toDto).toList();

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Registros obtenidos correctamente"),
                dtos,
                null
        );
    }

    @Transactional
    public void save(BatchReg batchReg) {
        batchRegRepository.save(batchReg);
    }

    @Transactional(readOnly = true)
    public BatchReg findByIdOrThrow(Long id) {
        return batchRegRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro no encontrado o eliminado"));
    }

    @Transactional
    public ApiResponse<Void> softDelete(Long id) {
        BatchReg reg = findByIdOrThrow(id);
        reg.setDeleted(true);
        batchRegRepository.save(reg);

        return errorFactory.buildSuccess(HttpStatus.OK, "Registro eliminado correctamente");
    }

    @Transactional(readOnly = true)
    public ApiResponse<Page<BatchRegResponseDto>> searchAdvanced(
            Long containerId,
            Long batchEncId,
            String createdByUsername,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Pageable pageable) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado")));
        }

        Page<BatchReg> page = batchRegRepository.searchAdvanced(
                containerId, batchEncId, createdByUsername, fechaInicio, fechaFin, pageable);

        Page<BatchRegResponseDto> dtoPage = page.map(this::toDto);

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Búsqueda avanzada de registros completada"),
                dtoPage,
                null
        );
    }

    private BatchRegResponseDto toDto(BatchReg reg) {
        return BatchRegResponseDto.builder()
                .id(reg.getId())
                .collectionDate(reg.getCollectionDate())
                .weight(reg.getWeight())
                .containerId(reg.getContainer().getId())
                .batchEncId(reg.getBatchEnc().getId())
                .createdByUsername(reg.getCreatedBy().getUserName())
                .build();
    }
}
