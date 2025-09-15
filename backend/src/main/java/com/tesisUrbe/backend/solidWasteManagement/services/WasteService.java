package com.tesisUrbe.backend.solidWasteManagement.services;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.entities.account.User;
import com.tesisUrbe.backend.entities.solidWaste.Batch;
import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.entities.solidWaste.Waste;
import com.tesisUrbe.backend.repositories.WasteRepository;
import com.tesisUrbe.backend.solidWasteManagement.dto.WasteRequestDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.WasteResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WasteService {

    private final WasteRepository wasteRepository;
    private final ContainerService containerService;
    private final BatchService batchService;
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

        Optional<Container> containerOpt = containerService.findById(dto.getContainerId());
        if (containerOpt.isEmpty()) {
            return errorFactory.build(HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("INVALID_CONTAINER", "containerId", "Contenedor no válido")));
        }

        Optional<Batch> batchOpt = batchService.findById(dto.getBatchId());
        if (batchOpt.isEmpty()) {
            return errorFactory.build(HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("INVALID_BATCH", "batchId", "Lote no válido")));
        }

        Waste waste = new Waste();
        waste.setWeight(dto.getWeight());
        waste.setCollectionDate(dto.getCollectionDate());
        waste.setContainer(containerOpt.get());
        waste.setBatch(batchOpt.get());
        waste.setCreatedBy(userOpt.get());

        wasteRepository.save(waste);
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
        if (wasteOpt.isEmpty()) {
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

        Page<Waste> wastePage = wasteRepository.findAll(pageable); // Puedes agregar filtros si lo deseas

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
}
