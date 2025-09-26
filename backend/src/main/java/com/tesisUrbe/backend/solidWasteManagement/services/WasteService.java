package com.tesisUrbe.backend.solidWasteManagement.services;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.entities.account.User;
import com.tesisUrbe.backend.entities.solidWaste.BatchEnc;
import com.tesisUrbe.backend.entities.solidWaste.BatchReg;
import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.entities.solidWaste.Waste;
import com.tesisUrbe.backend.solidWasteManagement.dto.WasteRequestDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.WasteResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.enums.BatchStatus;
import com.tesisUrbe.backend.solidWasteManagement.repository.WasteRepository;
import com.tesisUrbe.backend.usersManagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WasteService {

    private final WasteRepository wasteRepository;
    private final ContainerService containerService;
    private final BatchEncService batchEncService;
    private final BatchRegService batchRegService;
    private final UserService userService;
    private final ApiErrorFactory errorFactory;

    @Transactional
    public ApiResponse<Void> registerWaste(WasteRequestDto dto) {
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

        if (dto.getContainerId() == null || dto.getBatchId() == null) {
            return errorFactory.build(HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("MISSING_ID", null, "containerId y batchId son obligatorios")));
        }

        Optional<Container> containerOpt = containerService.findById(dto.getContainerId());
        if (containerOpt.isEmpty()) {
            return errorFactory.build(HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("INVALID_CONTAINER", "containerId", "Contenedor no válido")));
        }

        Optional<BatchEnc> batchOpt = batchEncService.findById(dto.getBatchId());
        if (batchOpt.isEmpty()) {
            return errorFactory.build(HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("INVALID_BATCH", "batchId", "Lote no válido")));
        }

        BatchEnc batch = batchOpt.get();
        if (batch.getStatus() != BatchStatus.IN_PROGRESS) {
            return errorFactory.build(HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("BATCH_CLOSED", "batchId", "No se puede agregar residuos a un lote cerrado o despachado")));
        }

        if (dto.getWeight() == null || dto.getWeight().compareTo(BigDecimal.ZERO) <= 0) {
            return errorFactory.build(HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("INVALID_WEIGHT", "weight", "El peso debe ser mayor a cero")));
        }

        Waste waste = new Waste();
        waste.setWeight(dto.getWeight());
        waste.setCollectionDate(LocalDateTime.now());
        waste.setContainer(containerOpt.get());
        waste.setBatch(batch);
        waste.setCreatedBy(userOpt.get());
        waste.setDeleted(false);
        wasteRepository.save(waste);

        BigDecimal updatedWeight = batch.getTotalWeight().add(dto.getWeight());
        batch.setTotalWeight(updatedWeight);
        batchEncService.save(batch);

        BatchReg reg = new BatchReg();
        reg.setWeight(dto.getWeight());
        reg.setCollectionDate(LocalDate.now());
        reg.setContainer(containerOpt.get());
        reg.setBatchEnc(batch);
        reg.setCreatedBy(userOpt.get());
        reg.setDeleted(false);
        batchRegService.save(reg);

        return errorFactory.buildSuccess(HttpStatus.CREATED, "Residuo registrado exitosamente");
    }


    @Transactional(readOnly = true)
    public ApiResponse<WasteResponseDto> getWasteById(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado")));
        }

        Optional<Waste> wasteOpt = wasteRepository.findById(id);
        if (wasteOpt.isEmpty() || wasteOpt.get().isDeleted()) {
            return errorFactory.build(HttpStatus.NOT_FOUND,
                    List.of(new ApiError("WASTE_NOT_FOUND", null, "Residuo no encontrado")));
        }

        Waste waste = wasteOpt.get();
        WasteResponseDto dto = new WasteResponseDto(
                waste.getId(),
                waste.getWeight(),
                waste.getCollectionDate(),
                waste.getContainer().getId(),
                waste.getBatch().getId()
        );

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Residuo obtenido correctamente"),
                dto,
                null
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<Page<WasteResponseDto>> getAllWaste(
            int page, int size, String sortBy, String sortDir, String search) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado")));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Waste> wastePage;

        if (StringUtils.hasText(search)) {
            wastePage = wasteRepository.findByDeletedFalseAndContainer_SerialContainingIgnoreCase(search, pageable);
        } else {
            wastePage = wasteRepository.findByDeletedFalse(pageable);
        }

        if (wastePage.isEmpty()) {
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("NO_WASTE_FOUND", null, "No se encontraron residuos para los criterios especificados"))
            );
        }

        Page<WasteResponseDto> dtoPage = wastePage.map(waste ->
                new WasteResponseDto(
                        waste.getId(),
                        waste.getWeight(),
                        waste.getCollectionDate(),
                        waste.getContainer().getId(),
                        waste.getBatch().getId()
                )
        );

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Residuos obtenidos correctamente"),
                dtoPage,
                null
        );
    }

    @Transactional
    public void save(Waste waste) {
        wasteRepository.save(waste);
    }
}
