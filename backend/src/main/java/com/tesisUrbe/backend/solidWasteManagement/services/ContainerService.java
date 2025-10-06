package com.tesisUrbe.backend.solidWasteManagement.services;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.common.util.ValidationUtils;
import com.tesisUrbe.backend.entities.account.User;
import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.entities.solidWaste.ContainerType;
import com.tesisUrbe.backend.solidWasteManagement.dto.ContainerRequestDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.ContainerResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.enums.ContainerStatus;
import com.tesisUrbe.backend.solidWasteManagement.repository.ContainerRepository;
import com.tesisUrbe.backend.usersManagement.services.UserService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ContainerService {
    private final ContainerTypeService containerTypeService;
    private final ContainerRepository containerRepository;
    private final ApiErrorFactory errorFactory;
    private final UserService userService;
    
    @org.springframework.beans.factory.annotation.Value("${app.frontend.base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    @org.springframework.beans.factory.annotation.Value("${app.qr.size:300}")
    private int frontendQrSize;

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

        if (!StringUtils.hasText(dto.getSerial())) {
            return errorFactory.build(
                    HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("MISSING_SERIAL", "serial", "El campo serial es obligatorio"))
            );
        }

        if (containerRepository.existsBySerial(dto.getSerial())) {
            return errorFactory.build(
                    HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("DUPLICATE_SERIAL", "serial", "Ya existe un contenedor con ese serial"))
            );
        }

        if (dto.getLatitude() == null || dto.getLongitude() == null || dto.getCapacity() == null) {
            return errorFactory.build(
                    HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("INVALID_COORDINATES", null, "Latitud, longitud y capacidad son obligatorios"))
            );
        }

        if (dto.getContainerTypeId() == null || dto.getContainerTypeId() <= 0) {
            return errorFactory.build(
                    HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("INVALID_CONTAINER_TYPE_ID", "containerTypeId", "ID de tipo de contenedor inválido"))
            );
        }

        ContainerStatus statusEnum;
        try {
            statusEnum = ContainerStatus.valueOf(dto.getStatus().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return errorFactory.build(
                    HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("INVALID_STATUS", "status", "Estado inválido. Valores permitidos: AVAILABLE, UNDER_MAINTENANCE, FULL"))
            );
        }

        Optional<ContainerType> typeOpt = containerTypeService.findById(dto.getContainerTypeId());

        if (typeOpt.isEmpty()) {
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("CONTAINER_TYPE_NOT_FOUND", "containerTypeId", "Tipo de contenedor no encontrado"))
            );
        }

        if (typeOpt.get().isDeleted()) {
            return errorFactory.build(
                    HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("CONTAINER_TYPE_DELETED", "containerTypeId", "Tipo de contenedor eliminado"))
            );
        }

        Container container = Container.builder()
                .serial(dto.getSerial())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .capacity(dto.getCapacity())
                .status(statusEnum)
                .containerType(typeOpt.get())
                .createdBy(userOpt.get())
                .deleted(false)
                .build();

        try {
            containerRepository.save(container);
            return errorFactory.buildSuccess(HttpStatus.CREATED, "Contenedor registrado exitosamente");
        } catch (Exception e) {
            return errorFactory.build(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    List.of(new ApiError("PERSISTENCE_ERROR", null, "Error interno al registrar el contenedor"))
            );
        }
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
                container.getSerial(),
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

    @Transactional(readOnly = true)
    public Optional<Container> findBySerialAndDeletedFalse(String serial) {
        if (serial == null || serial.isBlank()) {
            return Optional.empty();
        }
        return containerRepository.findBySerialAndDeletedFalse(serial);
    }

    public Optional<Container> findById(Long id) {
        return containerRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public ApiResponse<Page<ContainerResponseDto>> getAllContainers(
            int page, int size, String sortBy, String sortDir, String serial, Long id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return errorFactory.build(HttpStatus.UNAUTHORIZED,
                    List.of(new ApiError("UNAUTHORIZED", null, "No estás autenticado")));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Container> containerPage;

        if (id != null) {
            containerPage = containerRepository.findByIdAndDeletedFalse(id, pageable);
        } else if (StringUtils.hasText(serial)) {
            containerPage = containerRepository.findBySerialContainingIgnoreCaseAndDeletedFalse(serial, pageable);
        } else {
            containerPage = containerRepository.findByDeletedFalse(pageable);
        }

        Page<ContainerResponseDto> dtoPage = containerPage.map(container ->
                new ContainerResponseDto(
                        container.getId(),
                        container.getSerial(),
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

        if (id == null || id <= 0) {
            return errorFactory.build(
                    HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("INVALID_ID", "id", "ID de contenedor inválido"))
            );
        }

        Optional<Container> containerOpt = containerRepository.findById(id);

        if (containerOpt.isEmpty()) {
            return errorFactory.build(
                    HttpStatus.NOT_FOUND,
                    List.of(new ApiError("CONTAINER_NOT_FOUND", "id", "Contenedor no encontrado"))
            );
        }

        Container container = containerOpt.get();

        if (container.isDeleted()) {
            return errorFactory.build(
                    HttpStatus.BAD_REQUEST,
                    List.of(new ApiError("CONTAINER_DELETED", "id", "El contenedor fue eliminado"))
            );
        }

        if (StringUtils.hasText(dto.getSerial())) {
            if (!container.getSerial().equals(dto.getSerial()) && containerRepository.existsBySerial(dto.getSerial())) {
                return errorFactory.build(
                        HttpStatus.CONFLICT,
                        List.of(new ApiError("DUPLICATE_SERIAL", "serial", "Ya existe un contenedor con ese serial"))
                );
            }
            container.setSerial(dto.getSerial());
        }

        if (dto.getLatitude() != null) container.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) container.setLongitude(dto.getLongitude());

        if (dto.getCapacity() != null) {
            if (dto.getCapacity().compareTo(BigDecimal.ZERO) <= 0) {
                return errorFactory.build(
                        HttpStatus.BAD_REQUEST,
                        List.of(new ApiError("INVALID_CAPACITY", "capacity", "La capacidad debe ser mayor a cero"))
                );
            }
            container.setCapacity(dto.getCapacity());
        }

        if (StringUtils.hasText(dto.getStatus())) {
            try {
                ContainerStatus statusEnum = ContainerStatus.valueOf(dto.getStatus().toUpperCase());
                container.setStatus(statusEnum);
            } catch (IllegalArgumentException e) {
                return errorFactory.build(
                        HttpStatus.BAD_REQUEST,
                        List.of(new ApiError("INVALID_STATUS", "status",
                                ValidationUtils.buildValidEnumMessage(ContainerStatus.class, "Estado")))
                );
            }
        }

        if (dto.getContainerTypeId() != null && dto.getContainerTypeId() > 0) {
            Optional<ContainerType> typeOpt = containerTypeService.findById(dto.getContainerTypeId());

            if (typeOpt.isEmpty()) {
                return errorFactory.build(
                        HttpStatus.NOT_FOUND,
                        List.of(new ApiError("CONTAINER_TYPE_NOT_FOUND", "containerTypeId", "Tipo de contenedor no encontrado"))
                );
            }

            if (typeOpt.get().isDeleted()) {
                return errorFactory.build(
                        HttpStatus.BAD_REQUEST,
                        List.of(new ApiError("CONTAINER_TYPE_DELETED", "containerTypeId", "Tipo de contenedor eliminado"))
                );
            }

            container.setContainerType(typeOpt.get());
        }

        try {
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

    @Transactional(readOnly = true)
    public byte[] generateContainerQrById(Long id) {
        Optional<Container> containerOpt = containerRepository.findById(id);
        if (containerOpt.isEmpty() || containerOpt.get().isDeleted()) {
            return null;
        }

        Container container = containerOpt.get();

        // Build the frontend URL from injected property
        String path = String.format("/containers/%d", container.getId());
        String target = frontendBaseUrl + path;

        try {
            int size = frontendQrSize;

            Map<EncodeHintType, Object> hints = Map.of(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
            BitMatrix bitMatrix = new MultiFormatWriter().encode(target, BarcodeFormat.QR_CODE, size, size, hints);
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
                return baos.toByteArray();
            }
        } catch (Exception e) {
            return null;
        }
    }

}
