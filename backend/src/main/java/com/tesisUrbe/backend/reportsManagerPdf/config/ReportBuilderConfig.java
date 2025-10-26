package com.tesisUrbe.backend.reportsManagerPdf.config;

import com.tesisUrbe.backend.common.util.NormalizationUtils;
import com.tesisUrbe.backend.entities.setting.UniversitySetting;
import com.tesisUrbe.backend.reportsManagerPdf.builders.*;
import com.tesisUrbe.backend.reportsManagerPdf.registry.ReportRegistry;
import com.tesisUrbe.backend.settingsManagement.services.FileStorageService;
import com.tesisUrbe.backend.settingsManagement.services.SettingsService;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchEncResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchRegResponseDto;
import com.tesisUrbe.backend.solidWasteManagement.dto.ContainerResponseDto;
import com.tesisUrbe.backend.usersManagement.dto.AdminUserDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;

import java.util.List;

@Configuration
public class ReportBuilderConfig {


    @Bean
    public PdfHeaderBuilder pdfHeaderBuilder(SettingsService settingsService, FileStorageService fileStorageService) {
        return new PdfHeaderBuilder(settingsService, fileStorageService);
    }

    @Bean
    public PdfFooterBuilder pdfFooterBuilder() {
        return new PdfFooterBuilder();
    }

    @Bean
    public ReportModule<BatchRegResponseDto> batchReportModule(
            SettingsService settingsService,
            PdfHeaderBuilder header,
            PdfFooterBuilder footer
    ) {
        RowMapper<BatchRegResponseDto> mapper = reg -> List.of(
                reg.getSerial(),
                String.format("%.2f", reg.getWeight()),
                reg.getDate(),
                reg.getHour() != null ? reg.getHour().substring(0, 8) : "",
                reg.getCreatedByUsername()
        );

        PdfTableBuilderInterface<BatchRegResponseDto> table = new PdfTableBuilder<>(settingsService, mapper);
        ReportBuilder<BatchRegResponseDto> builder = new PdfReportBuilder<>(header, footer, table);

        return new ReportModule<>(mapper, table, builder);
    }

    @Bean
    public ReportModule<BatchEncResponseDto> batchEncReportModule(
            SettingsService settingsService,
            PdfHeaderBuilder header,
            PdfFooterBuilder footer
    ) {
        RowMapper<BatchEncResponseDto> mapper = dto -> List.of(
                String.valueOf(dto.getId()),
                dto.getCreationDate() != null
                        ? dto.getCreationDate()
                        : "",
                dto.getDescription() != null ? dto.getDescription().replaceAll("[\\r\\n]+", " ") : "",
                dto.getTotalWeight() != null ? dto.getTotalWeight().toPlainString() : "0",
                dto.getStatus() != null
                        ? dto.getStatus()
                        : "",
                dto.getProcessedAt() != null
                        ? dto.getProcessedAt()
                        : "-",
                dto.getCreatedByUsername() != null ? dto.getCreatedByUsername() : "",
                dto.getProcessedByUsername() != null ? dto.getProcessedByUsername() : "-"
        );


        PdfTableBuilderInterface<BatchEncResponseDto> table = new PdfTableBuilder<>(settingsService, mapper);
        ReportBuilder<BatchEncResponseDto> builder = new PdfReportBuilder<>(header, footer, table);

        return new ReportModule<>(mapper, table, builder);
    }

    @Bean
    public ReportModule<AdminUserDto> adminReportModule(
            SettingsService settingsService,
            PdfHeaderBuilder header,
            PdfFooterBuilder footer
    ) {
        RowMapper<AdminUserDto> mapper = user -> List.of(
                user.getId() != null ? user.getId().toString() : "",
                user.getFullName() != null ? user.getFullName().replaceAll("[\\r\\n]+", " ") : "",
                user.getUserName() != null ? user.getUserName() : "",
                user.getEmail() != null ? user.getEmail() : "",
                user.getRole() != null ? user.getRole() : "",
                user.isVerified() ? "Sí" : "No",
                user.isAccountLocked() ? "Sí" : "No",
                user.isUserLocked() ? "Sí" : "No"
        );

        PdfTableBuilderInterface<AdminUserDto> table = new PdfTableBuilder<>(settingsService, mapper);
        ReportBuilder<AdminUserDto> builder = new PdfReportBuilder<>(header, footer, table);

        return new ReportModule<>(mapper, table, builder);
    }

    @Bean
    public ReportModule<ContainerResponseDto> containerReportModule(
            SettingsService settingsService,
            PdfHeaderBuilder header,
            PdfFooterBuilder footer
    ) {
        RowMapper<ContainerResponseDto> mapper = dto -> List.of(
                dto.getId() != null ? dto.getId().toString() : "",
                dto.getSerial() != null ? dto.getSerial() : "",
                dto.getLatitude() != null ? dto.getLatitude().toPlainString() : "",
                dto.getLongitude() != null ? dto.getLongitude().toPlainString() : "",
                dto.getCapacity() != null ? dto.getCapacity().toPlainString() : "",
                dto.getStatus() != null ? dto.getStatus() : "",
                dto.getContainerTypeName() != null ? dto.getContainerTypeName() : "",
                dto.getCreatedAt() != null ? NormalizationUtils.formatDateTime(dto.getCreatedAt()) : ""
        );

        PdfTableBuilderInterface<ContainerResponseDto> table = new PdfTableBuilder<>(settingsService, mapper);
        ReportBuilder<ContainerResponseDto> builder = new PdfReportBuilder<>(header, footer, table);

        return new ReportModule<>(mapper, table, builder);
    }

    @Bean
    public CommandLineRunner registerReportModules(
            ReportRegistry registry,
            ReportModule<BatchRegResponseDto> batchModule,
            ReportModule<AdminUserDto> adminModule,
            ReportModule<BatchEncResponseDto> batchEncModule,
            ReportModule<ContainerResponseDto> containerModule
    ) {
        return args -> {
            registry.register(BatchRegResponseDto.class, batchModule.reportBuilder());
            registry.register(AdminUserDto.class, adminModule.reportBuilder());
            registry.register(BatchEncResponseDto.class, batchEncModule.reportBuilder());
            registry.register(ContainerResponseDto.class, containerModule.reportBuilder());
        };
    }

}
