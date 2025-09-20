package com.tesisUrbe.backend.solidWasteManagement.services;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.entities.account.User;
import com.tesisUrbe.backend.entities.solidWaste.Batch;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchRequestDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.repository.BatchRepository;
import com.tesisUrbe.backend.users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;
    private final UserService userService;
    private final ApiErrorFactory errorFactory;

    @Transactional
    public ApiResponse<Void> registerBatch(BatchRequestDto dto) {
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

        Batch batch = new Batch();
        batch.setCreationDate(dto.getCreationDate());
        batch.setDescription(dto.getDescription());
        batch.setTotalWeight(dto.getTotalWeight());
        batch.setStatus(dto.getStatus());
        batch.setShippingDate(dto.getShippingDate());
        batch.setCreatedBy(userOpt.get());

        batchRepository.save(batch);
        return errorFactory.buildSuccess(HttpStatus.CREATED, "Lote registrado exitosamente");
    }

    @Transactional(readOnly = true)
    public ApiResponse<BatchResponseDto> getBatchById(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado")));
        }

        Optional<Batch> batchOpt = batchRepository.findById(id);

        if (batchOpt.isEmpty()) {
            return errorFactory.build(HttpStatus.NOT_FOUND,
                    List.of(new ApiError("BATCH_NOT_FOUND", null, "Lote no encontrado")));
        }

        Batch batch = batchOpt.get();

        BatchResponseDto dto = new BatchResponseDto(
                batch.getId(),
                batch.getCreationDate(),
                batch.getDescription(),
                batch.getTotalWeight(),
                batch.getStatus(),
                batch.getShippingDate()
        );

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Lote obtenido correctamente"),
                dto,
                null
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<Page<BatchResponseDto>> getAllBatches(
            int page, int size, String sortBy, String sortDir) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado")));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Batch> batchPage = batchRepository.findAll(pageable);

        Page<BatchResponseDto> dtoPage = batchPage.map(batch ->
                new BatchResponseDto(
                        batch.getId(),
                        batch.getCreationDate(),
                        batch.getDescription(),
                        batch.getTotalWeight(),
                        batch.getStatus(),
                        batch.getShippingDate()
                )
        );

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Lotes obtenidos correctamente"),
                dtoPage,
                null
        );
    }

    @Transactional(readOnly = true)
    public Optional<Batch> findById(Long id) {
        return batchRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return batchRepository.existsById(id);
    }
}
