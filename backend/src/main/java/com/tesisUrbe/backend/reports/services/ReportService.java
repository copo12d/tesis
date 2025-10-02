package com.tesisUrbe.backend.reports.services;

import com.tesisUrbe.backend.entities.account.User;
import com.tesisUrbe.backend.entities.enums.RoleList;
import com.tesisUrbe.backend.entities.solidWaste.BatchEnc;
import com.tesisUrbe.backend.entities.solidWaste.BatchReg;
import com.tesisUrbe.backend.reports.builders.ReportBuilder;
import com.tesisUrbe.backend.reports.registry.ReportRegistry;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchRegResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.repository.BatchEncRepository;
import com.tesisUrbe.backend.solidWasteManagement.repository.BatchRegRepository;
import com.tesisUrbe.backend.usersManagement.dto.AdminUserDto;
import com.tesisUrbe.backend.usersManagement.repository.UserRepository;
import com.tesisUrbe.backend.usersManagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    public ResponseEntity<byte[]> generateAllUsersPdf(
            String role,
            Boolean verified,
            Boolean accountLocked,
            Boolean userLocked,
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

        List<User> users = userRepository.searchAdvancedForReport(
                roleEnum, verified, accountLocked, userLocked
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
