package com.tesisUrbe.backend.reportsManagerPdf.services;

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

    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> generateBatchReportPdf(Long batchId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<BatchEnc> batchOpt = batchEncRepository.findByIdAndDeletedFalse(batchId);
        List<BatchReg> regs = batchRegRepository.findByBatchEncIdAndDeletedFalse(batchId);

        if (batchOpt.isEmpty() || regs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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

            String filename = "batch-report-" + batchId + ".pdf";

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + filename)
                    .header("Content-Type", "application/pdf")
                    .body(pdf);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> generateBatchEncReport(String fechaInicio, String fechaFin) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
            return ResponseEntity.badRequest().build();
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=batch-enc-report.pdf")
                    .header("Content-Type", "application/pdf")
                    .body(pdf);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

        @Transactional(readOnly = true)
        public ResponseEntity<byte[]> generateAllUsersPdf(
                String role,
                String verified,
                String accountLocked,
                String userLocked,
                String sortBy,
                String sortDir
        ) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
                    return ResponseEntity.badRequest().build();
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
                            user.getRole().getName().name(),
                            user.isVerified(),
                            user.isAccountLocked(),
                            user.isUserLocked()
                    ))
                    .toList();

            if (dtos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            try {
                List<String> columnTitles = List.of(
                        "ID", "Nombre completo", "Usuario", "Correo", "Rol",
                        "Verificado", "Bloqueo de cuenta", "Bloqueo de usuario"
                );

                ReportBuilder<AdminUserDto> builder = reportRegistry.getBuilder(AdminUserDto.class);
                byte[] pdf = builder.build("Reporte de Usuarios Administradores", columnTitles, dtos, auth.getName());

                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=\"admin-users-report.pdf\"")
                        .header("Content-Type", "application/pdf")
                        .body(pdf);

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> generateContainerReport(
            String serial, Long id, String sortBy, String sortDir
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Comparator<Container> comparator = switch (sortBy) {
            case "serial" -> Comparator.comparing(Container::getSerial, Comparator.nullsLast(String::compareToIgnoreCase));
            case "createdAt" -> Comparator.comparing(Container::getCreatedAt, Comparator.nullsLast(LocalDateTime::compareTo));
            default -> Comparator.comparing(Container::getId);
        };
        if ("DESC".equalsIgnoreCase(sortDir)) {
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
                .toList();

        try {
            List<String> columnTitles = List.of(
                    "ID", "Serial", "Latitud", "Longitud", "Capacidad",
                    "Estado", "Tipo de contenedor", "Creado en"
            );

            ReportBuilder<ContainerResponseDto> builder = reportRegistry.getBuilder(ContainerResponseDto.class);
            byte[] pdf = builder.build("Reporte de Contenedores", columnTitles, dtos, auth.getName());

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"containers-report.pdf\"")
                    .header("Content-Type", "application/pdf")
                    .body(pdf);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
