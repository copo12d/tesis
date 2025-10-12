package com.tesisUrbe.backend.reportsManagerPdf.services;

import com.tesisUrbe.backend.common.exception.ApiError;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.entities.account.User;
import com.tesisUrbe.backend.entities.enums.RoleList;
import com.tesisUrbe.backend.entities.solidWaste.BatchEnc;
import com.tesisUrbe.backend.entities.solidWaste.BatchReg;
import com.tesisUrbe.backend.entities.solidWaste.Container;
import com.tesisUrbe.backend.reportsManagerPdf.builders.ReportBuilder;
import com.tesisUrbe.backend.reportsManagerPdf.registry.ReportRegistry;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchEncResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchRegResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.ContainerResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.repository.BatchEncRepository;
import com.tesisUrbe.backend.solidWasteManagement.repository.BatchRegRepository;
import com.tesisUrbe.backend.solidWasteManagement.repository.ContainerRepository;
import com.tesisUrbe.backend.usersManagement.dto.AdminUserDto;
import com.tesisUrbe.backend.usersManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRegistry reportRegistry;
    private final UserRepository userRepository;
    private final BatchEncRepository batchEncRepository;
    private final BatchRegRepository batchRegRepository;
    private final ContainerRepository containerRepository;
    private final ApiErrorFactory errorFactory;

    @Transactional(readOnly = true)
    public ApiResponse<byte[]> generateBatchReportPdf(Long batchId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.UNAUTHORIZED, "No estás autenticado"),
                    null,
                    List.of(new ApiError("UNAUTHORIZED", null, "Debes iniciar sesión para generar el reporte"))
            );
        }

        Optional<BatchEnc> batchOpt = batchEncRepository.findByIdAndDeletedFalse(batchId);
        List<BatchReg> regs = batchRegRepository.findByBatchEncIdAndDeletedFalse(batchId);

        if (batchOpt.isEmpty() || regs.isEmpty()) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.NOT_FOUND, "Lote no encontrado o sin registros"),
                    null,
                    List.of(new ApiError("BATCH_NOT_FOUND", "batchId", "No se encontró el lote con ID " + batchId + " o no tiene registros"))
            );
        }

        List<BatchRegResponseDto> dtos = regs.stream()
                .map(reg -> BatchRegResponseDto.builder()
                        .serial(reg.getContainer().getSerial())
                        .weight(reg.getWeight())
                        .date(reg.getCollectionDate().toLocalDate().toString())
                        .hour(reg.getCollectionDate().toLocalTime().toString())
                        .createdByUsername(reg.getCreatedBy().getUserName())
                        .build())
                .collect(Collectors.toList());

        try {
            List<String> columnTitles = List.of("Serial", "Peso (Kg)", "Fecha", "Hora", "Recolector");

            ReportBuilder<BatchRegResponseDto> builder = reportRegistry.getBuilder(BatchRegResponseDto.class);
            byte[] pdf = builder.build("Reporte de Lotes", columnTitles, dtos, auth.getName());

            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.OK, "Reporte PDF generado exitosamente"),
                    pdf,
                    null
            );

        } catch (Exception e) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar el PDF"),
                    null,
                    List.of(new ApiError("PDF_GENERATION_ERROR", null, "Error interno al generar el reporte PDF"))
            );
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<byte[]> generateBatchEncReport(String fechaInicio, String fechaFin) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.UNAUTHORIZED, "No estás autenticado"),
                    null,
                    List.of(new ApiError("UNAUTHORIZED", null, "Debes iniciar sesión para generar el reporte"))
            );
        }

        LocalDate fechaInicioDate = null;
        LocalDate fechaFinDate = null;

        try {
            if (StringUtils.hasText(fechaInicio)) {
                fechaInicioDate = LocalDate.parse(fechaInicio, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            if (StringUtils.hasText(fechaFin)) {
                fechaFinDate = LocalDate.parse(fechaFin, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        } catch (Exception e) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.BAD_REQUEST, "Fechas inválidas"),
                    null,
                    List.of(new ApiError("INVALID_DATE_FORMAT", null, "Las fechas deben tener formato yyyy-MM-dd"))
            );
        }

        List<BatchEnc> batches;
        if (fechaInicioDate != null && fechaFinDate != null) {
            batches = batchEncRepository.findByCreationDateBetweenAndDeletedFalse(fechaInicioDate, fechaFinDate);
        } else if (fechaInicioDate != null) {
            batches = batchEncRepository.findByCreationDateGreaterThanEqualAndDeletedFalse(fechaInicioDate);
        } else if (fechaFinDate != null) {
            batches = batchEncRepository.findByCreationDateLessThanEqualAndDeletedFalse(fechaFinDate);
        } else {
            batches = batchEncRepository.findByDeletedFalse();
        }

        if (batches.isEmpty()) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.NOT_FOUND, "No se encontraron lotes"),
                    null,
                    List.of(new ApiError("BATCHES_NOT_FOUND", null, "No hay lotes registrados en el rango solicitado"))
            );
        }

        List<BatchEncResponseDto> dtos = batches.stream()
                .map(batch -> BatchEncResponseDto.builder()
                        .id(batch.getId())
                        .creationDate(batch.getCreationDate())
                        .description(batch.getDescription())
                        .totalWeight(batch.getTotalWeight())
                        .status(batch.getStatus())
                        .processedAt(batch.getProcessedAt())
                        .createdByUsername(batch.getCreatedBy() != null ? batch.getCreatedBy().getUserName() : "")
                        .processedByUsername(batch.getProcessedBy() != null ? batch.getProcessedBy().getUserName() : null)
                        .build())
                .toList();

        try {
            List<String> columnTitles = List.of("ID", "Fecha", "Descripción", "Peso Total", "Estado", "Procesado", "Creado por", "Procesado por");

            ReportBuilder<BatchEncResponseDto> builder = reportRegistry.getBuilder(BatchEncResponseDto.class);
            byte[] pdf = builder.build("Reporte de Lotes", columnTitles, dtos, auth.getName());

            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.OK, "Reporte PDF generado exitosamente"),
                    pdf,
                    null
            );

        } catch (Exception e) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar el PDF"),
                    null,
                    List.of(new ApiError("PDF_GENERATION_ERROR", null, "Error interno al generar el reporte PDF"))
            );
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<byte[]> generateAllUsersPdf(
            String role,
            String verified,
            String accountLocked,
            String userLocked,
            String sortBy,
            String sortDir
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.UNAUTHORIZED, "No estás autenticado"),
                    null,
                    List.of(new ApiError("UNAUTHORIZED", null, "Debes iniciar sesión para generar el reporte"))
            );
        }

        String callerRole = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        RoleList roleEnum = null;
        if (StringUtils.hasText(role)) {
            try {
                roleEnum = RoleList.valueOf(role);
            } catch (IllegalArgumentException e) {
                return new ApiResponse<>(
                        errorFactory.buildMeta(HttpStatus.BAD_REQUEST, "Rol inválido"),
                        null,
                        List.of(new ApiError("INVALID_ROLE", "role", "El rol especificado no es válido"))
                );
            }
        }

        Boolean verifiedBool = StringUtils.hasText(verified) ? Boolean.valueOf(verified) : null;
        Boolean accountLockedBool = StringUtils.hasText(accountLocked) ? Boolean.valueOf(accountLocked) : null;
        Boolean userLockedBool = StringUtils.hasText(userLocked) ? Boolean.valueOf(userLocked) : null;

        List<User> users = userRepository.searchAdvancedForReport(
                roleEnum, verifiedBool, accountLockedBool, userLockedBool
        );

        List<AdminUserDto> dtos = users.stream()
                .filter(user -> callerRole.equals("ROLE_SUPERUSER") || user.getRole().getName() != RoleList.ROLE_SUPERUSER)
                .map(user -> new AdminUserDto(
                        user.getId(),
                        user.getFullName(),
                        user.getUserName(),
                        user.getEmail(),
                        user.getRole().getName().getDescription(),
                        user.isVerified(),
                        user.isAccountLocked(),
                        user.isUserLocked()
                ))
                .collect(Collectors.toCollection(ArrayList::new));

        if (dtos.isEmpty()) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.NOT_FOUND, "No se encontraron usuarios"),
                    null,
                    List.of(new ApiError("USERS_NOT_FOUND", null, "No hay usuarios que coincidan con los filtros aplicados"))
            );
        }

        // Ordenamiento seguro
        String sortField = StringUtils.hasText(sortBy) ? sortBy : "id";
        String direction = StringUtils.hasText(sortDir) ? sortDir.toUpperCase() : "ASC";

        Comparator<AdminUserDto> comparator = switch (sortField) {
            case "fullName" -> Comparator.comparing(
                    AdminUserDto::getFullName,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );
            case "userName" -> Comparator.comparing(
                    AdminUserDto::getUserName,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );
            case "email" -> Comparator.comparing(
                    AdminUserDto::getEmail,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );
            case "role" -> Comparator.comparing(
                    AdminUserDto::getRole,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );
            default -> Comparator.comparing(AdminUserDto::getId);
        };

        if ("DESC".equals(direction)) {
            comparator = comparator.reversed();
        }

        dtos.sort(comparator);

        try {
            List<String> columnTitles = List.of(
                    "ID", "Nombre completo", "Usuario", "Correo", "Rol",
                    "Verificado", "Bloqueo de cuenta", "Bloqueo de usuario"
            );

            ReportBuilder<AdminUserDto> builder = reportRegistry.getBuilder(AdminUserDto.class);
            byte[] pdf = builder.build("Reporte de Usuarios Administradores", columnTitles, dtos, auth.getName());

            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.OK, "Reporte PDF generado exitosamente"),
                    pdf,
                    null
            );

        } catch (Exception e) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar el PDF"),
                    null,
                    List.of(new ApiError("PDF_GENERATION_ERROR", null, "Error interno al generar el reporte PDF"))
            );
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<byte[]> generateContainerReport(
            String serial, Long id, String sortBy, String sortDir
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.UNAUTHORIZED, "No estás autenticado"),
                    null,
                    List.of(new ApiError("UNAUTHORIZED", null, "Debes iniciar sesión para generar el reporte"))
            );
        }

        List<Container> containers;
        if (id != null) {
            containers = containerRepository.findAllByIdAndDeletedFalse(id);
        } else if (StringUtils.hasText(serial)) {
            containers = containerRepository.findAllBySerialContainingIgnoreCaseAndDeletedFalse(serial);
        } else {
            containers = containerRepository.findAllByDeletedFalse();
        }

        if (containers.isEmpty()) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.NOT_FOUND, "No se encontraron contenedores"),
                    null,
                    List.of(new ApiError("CONTAINERS_NOT_FOUND", null, "No hay contenedores que coincidan con los filtros aplicados"))
            );
        }

        String sortField = StringUtils.hasText(sortBy) ? sortBy : "id";
        String direction = StringUtils.hasText(sortDir) ? sortDir.toUpperCase() : "ASC";

        Comparator<Container> comparator = switch (sortField) {
            case "serial" -> Comparator.comparing(
                    Container::getSerial,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );
            case "createdAt" -> Comparator.comparing(
                    Container::getCreatedAt,
                    Comparator.nullsLast(LocalDateTime::compareTo)
            );
            case "capacity" -> Comparator.comparing(
                    Container::getCapacity,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case "status" -> Comparator.comparing(
                    container -> container.getStatus() != null ? container.getStatus().name() : null,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );
            case "containerType" -> Comparator.comparing(
                    container -> container.getContainerType() != null ? container.getContainerType().getName() : null,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );
            default -> Comparator.comparing(Container::getId);
        };

        if ("DESC".equals(direction)) {
            comparator = comparator.reversed();
        }

        containers.sort(comparator);

        List<ContainerResponseDto> dtos = containers.stream()
                .map(container -> new ContainerResponseDto(
                        container.getId(),
                        container.getSerial(),
                        container.getLatitude(),
                        container.getLongitude(),
                        container.getCapacity(),
                        container.getStatus(),
                        container.getContainerType().getName(),
                        container.getCreatedAt()
                ))
                .collect(Collectors.toCollection(ArrayList::new)); // mutable para evitar errores

        try {
            List<String> columnTitles = List.of(
                    "ID", "Serial", "Latitud", "Longitud", "Capacidad",
                    "Estado", "Tipo de contenedor", "Creado en"
            );

            ReportBuilder<ContainerResponseDto> builder = reportRegistry.getBuilder(ContainerResponseDto.class);
            byte[] pdf = builder.build("Reporte de Contenedores", columnTitles, dtos, auth.getName());

            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.OK, "Reporte PDF generado exitosamente"),
                    pdf,
                    null
            );

        } catch (Exception e) {
            return new ApiResponse<>(
                    errorFactory.buildMeta(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar el PDF"),
                    null,
                    List.of(new ApiError("PDF_GENERATION_ERROR", null, "Error interno al generar el reporte PDF"))
            );
        }
    }

}
