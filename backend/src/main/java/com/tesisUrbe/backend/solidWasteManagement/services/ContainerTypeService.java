package com.tesisUrbe.backend.solidWasteManagement.services;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.common.util.ValidationUtils;
import com.tesisUrbe.backend.entities.solidWaste.ContainerType;
import com.tesisUrbe.backend.solidWasteManagement.dto.ContainerTypeRequestDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.ContainerTypeResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.UpdateContainerTypeDto;
import com.tesisUrbe.backend.solidWasteManagement.repository.ContainerTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContainerTypeService {

    private final ContainerTypeRepository containerTypeRepository;
    private final ApiErrorFactory errorFactory;

    @Transactional
    public ApiResponse<Void> registerContainerType(ContainerTypeRequestDto dto) {
        ValidationUtils.validateRequiredFields(dto);
        if (containerTypeRepository.existsByName(dto.getName())) {
            return errorFactory.build(
                    HttpStatus.CONFLICT,
                    List.of(new ApiError("CONTAINER_TYPE_EXISTS", "name", "Ya existe un tipo de contenedor con ese nombre"))
            );
        }
        ContainerType containerType = ContainerType.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .deleted(false)
                .build();
        containerTypeRepository.save(containerType);
        return errorFactory.buildSuccess(HttpStatus.CREATED, "Tipo de contenedor registrado exitosamente");
    }

    @Transactional(readOnly = true)
    public Optional<ContainerType> findById(Long id) {
        return containerTypeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public ApiResponse<ContainerTypeResponseDto> getContainerTypeById(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(
                    HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado"))
            );
        }

        ContainerType containerType = containerTypeRepository.findById(id).orElse(null);

        if (containerType == null || containerType.isDeleted()) {
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("CONTAINER_TYPE_NOT_FOUND", null, "Tipo de contenedor no encontrado o eliminado"))
            );
        }

        ContainerTypeResponseDto dto = new ContainerTypeResponseDto(
                containerType.getId(),
                containerType.getName(),
                containerType.getDescription()
        );

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Tipo de contenedor obtenido correctamente"),
                dto,
                null
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<Page<ContainerTypeResponseDto>> getAllContainerTypes(
            int page, int size, String sortBy, String sortDir, String search) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(
                    HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado"))
            );
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ContainerType> containerTypePage = StringUtils.hasText(search)
                ? containerTypeRepository.searchContainerTypes(search, pageable)
                : containerTypeRepository.findAllActive(pageable);

        Page<ContainerTypeResponseDto> dtoPage = containerTypePage.map(type ->
                new ContainerTypeResponseDto(
                        type.getId(),
                        type.getName(),
                        type.getDescription()
                )
        );

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Tipos de contenedor obtenidos correctamente"),
                dtoPage,
                null
        );
    }

    @Transactional
    public ApiResponse<Void> updateContainerType(Long id, UpdateContainerTypeDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(
                    HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado"))
            );
        }

        boolean hasPermission = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_SUPERUSER"));

        if (!hasPermission) {
            return errorFactory.build(
                    HttpStatus.FORBIDDEN,
                    List.of(new ApiError("ACCESS_DENIED", null, "Solo administradores o superusuarios pueden modificar tipos de contenedor"))
            );
        }

        Optional<ContainerType> containerTypeOpt = containerTypeRepository.findById(id);

        if (containerTypeOpt.isEmpty() || containerTypeOpt.get().isDeleted()) {
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("CONTAINER_TYPE_NOT_FOUND", null, "Tipo de contenedor no encontrado o eliminado"))
            );
        }

        ContainerType containerType = containerTypeOpt.get();

        try {
            if (dto.getName() != null && !dto.getName().isBlank()) {
                String normalizedName = dto.getName().trim();
                if (!containerType.getName().equalsIgnoreCase(normalizedName)
                        && containerTypeRepository.existsByName(normalizedName)) {
                    return errorFactory.build(
                            HttpStatus.CONFLICT,
                            List.of(new ApiError("CONTAINER_TYPE_EXISTS", "name", "Ya existe un tipo de contenedor con ese nombre"))
                    );
                }
                containerType.setName(normalizedName);
            }

            if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
                containerType.setDescription(dto.getDescription().trim());
            }

            containerTypeRepository.save(containerType);
            return errorFactory.buildSuccess(HttpStatus.OK, "Tipo de contenedor actualizado exitosamente");

        } catch (Exception e) {
            return errorFactory.build(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    List.of(new ApiError("PERSISTENCE_ERROR", null, "Error interno al actualizar el tipo de contenedor"))
            );
        }
    }

    @Transactional
    public ApiResponse<Void> softDeleteContainerType(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(
                    HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado"))
            );
        }

        boolean isSuperUser = auth.getAuthorities().stream()
                .anyMatch(granted -> granted.getAuthority().equals("ROLE_SUPERUSER"));

        if (!isSuperUser) {
            return errorFactory.build(
                    HttpStatus.FORBIDDEN,
                    List.of(new ApiError("ACCESS_DENIED", null, "Solo un superusuario puede eliminar tipos de contenedor"))
            );
        }

        Optional<ContainerType> containerTypeOpt = containerTypeRepository.findById(id);

        if (containerTypeOpt.isEmpty() || containerTypeOpt.get().isDeleted()) {
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("CONTAINER_TYPE_NOT_FOUND", null, "Tipo de contenedor no encontrado o ya eliminado"))
            );
        }

        ContainerType containerType = containerTypeOpt.get();
        containerType.setDeleted(true);

        try {
            containerTypeRepository.save(containerType);
            return errorFactory.buildSuccess(HttpStatus.OK, "Tipo de contenedor eliminado exitosamente");
        } catch (Exception e) {
            return errorFactory.build(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    List.of(new ApiError("PERSISTENCE_ERROR", null, "Error interno al eliminar el tipo de contenedor"))
            );
        }
    }

}

