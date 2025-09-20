package com.tesisUrbe.backend.solidWasteManagement.services;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.entities.account.User;
import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.entities.solidWaste.ContainerType;
import com.tesisUrbe.backend.solidWasteManagement.dto.ContainerRequestDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.ContainerResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.repository.ContainerRepository;
import com.tesisUrbe.backend.users.services.UserService;
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
public class ContainerService {
    private final ContainerTypeService containerTypeService;
    private final ContainerRepository containerRepository;
    private final ApiErrorFactory errorFactory;
    private final UserService userService;

    @Transactional
    public ApiResponse<Void> registerContainer(ContainerRequestDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(
                    HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado"))
            );
        }

        String username = auth.getName();
        Optional<User> userOpt = userService.findByUserName(username);

        if (userOpt.isEmpty()) {
            return errorFactory.build(
                    HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("USER_NOT_FOUND", null, "Usuario autenticado no válido"))
            );
        }

        Optional<ContainerType> typeOpt = containerTypeService.findById(dto.getContainerTypeId());

        if (typeOpt.isEmpty() || typeOpt.get().isDeleted()) {
            return errorFactory.build(
                    HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("INVALID_CONTAINER_TYPE", "containerTypeId", "Tipo de contenedor inválido o eliminado"))
            );
        }

        Container container = Container.builder()
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .capacity(dto.getCapacity())
                .status(dto.getStatus())
                .containerType(typeOpt.get())
                .createdBy(userOpt.get())
                .deleted(false)
                .build();

        containerRepository.save(container);
        return errorFactory.buildSuccess(HttpStatus.CREATED, "Contenedor registrado exitosamente");
    }

    @Transactional(readOnly = true)
    public ApiResponse<ContainerResponseDto> getContainerById(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(
                    HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado"))
            );
        }

        Optional<Container> containerOpt = containerRepository.findById(id);

        if (containerOpt.isEmpty() || containerOpt.get().isDeleted()) {
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("CONTAINER_NOT_FOUND", null, "Contenedor no encontrado o eliminado"))
            );
        }

        Container container = containerOpt.get();

        ContainerResponseDto dto = new ContainerResponseDto(
                container.getId(),
                container.getLatitude(),
                container.getLongitude(),
                container.getCapacity(),
                container.getStatus(),
                container.getContainerType().getName(),
                container.getCreatedAt()
        );

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Contenedor obtenido correctamente"),
                dto,
                null
        );
    }

    public Optional<Container> findById(Long id) {
        return containerRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public ApiResponse<Page<ContainerResponseDto>> getAllContainers(
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

        Page<Container> containerPage = StringUtils.hasText(search)
                ? containerRepository.findByDeletedFalseAndContainerType_NameContainingIgnoreCase(search, pageable)
                : containerRepository.findByDeletedFalse(pageable);

        Page<ContainerResponseDto> dtoPage = containerPage.map(container ->
                new ContainerResponseDto(
                        container.getId(),
                        container.getLatitude(),
                        container.getLongitude(),
                        container.getCapacity(),
                        container.getStatus(),
                        container.getContainerType().getName(),
                        container.getCreatedAt()
                )
        );

        return new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Contenedores obtenidos correctamente"),
                dtoPage,
                null
        );
    }

    @Transactional
    public ApiResponse<Void> updateContainer(Long id, ContainerRequestDto dto) {
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
                    List.of(new ApiError("ACCESS_DENIED", null, "Solo administradores o superusuarios pueden modificar contenedores"))
            );
        }

        Optional<Container> containerOpt = containerRepository.findById(id);

        if (containerOpt.isEmpty() || containerOpt.get().isDeleted()) {
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("CONTAINER_NOT_FOUND", null, "Contenedor no encontrado o eliminado"))
            );
        }

        Optional<ContainerType> typeOpt = containerTypeService.findById(dto.getContainerTypeId());

        if (typeOpt.isEmpty() || typeOpt.get().isDeleted()) {
            return errorFactory.build(
                    HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("INVALID_CONTAINER_TYPE", "containerTypeId", "Tipo de contenedor inválido o eliminado"))
            );
        }

        Container container = containerOpt.get();

        try {
            container.setLatitude(dto.getLatitude());
            container.setLongitude(dto.getLongitude());
            container.setCapacity(dto.getCapacity());
            container.setStatus(dto.getStatus());
            container.setContainerType(typeOpt.get());

            containerRepository.save(container);
            return errorFactory.buildSuccess(HttpStatus.OK, "Contenedor actualizado exitosamente");

        } catch (Exception e) {
            return errorFactory.build(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    List.of(new ApiError("PERSISTENCE_ERROR", null, "Error interno al actualizar el contenedor"))
            );
        }
    }

    @Transactional
    public ApiResponse<Void> softDeleteContainer(Long id) {
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
                    List.of(new ApiError("ACCESS_DENIED", null, "Solo un superusuario puede eliminar contenedores"))
            );
        }

        Optional<Container> containerOpt = containerRepository.findById(id);

        if (containerOpt.isEmpty() || containerOpt.get().isDeleted()) {
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("CONTAINER_NOT_FOUND", null, "Contenedor no encontrado o ya eliminado"))
            );
        }

        Container container = containerOpt.get();
        container.setDeleted(true);

        try {
            containerRepository.save(container);
            return errorFactory.buildSuccess(HttpStatus.OK, "Contenedor eliminado exitosamente");
        } catch (Exception e) {
            return errorFactory.build(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    List.of(new ApiError("PERSISTENCE_ERROR", null, "Error interno al eliminar el contenedor"))
            );
        }
    }

}
