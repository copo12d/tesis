package com.tesisUrbe.backend.reports.config;

import com.tesisUrbe.backend.reports.builders.*;
import com.tesisUrbe.backend.reports.registry.ReportRegistry;
import com.tesisUrbe.backend.solidWasteManagement.dto.BatchRegResponseDto;
import com.tesisUrbe.backend.usersManagement.dto.AdminUserDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;

import java.util.List;

@Configuration
public class ReportBuilderConfig {

    @Bean
    public ReportModule<BatchRegResponseDto> batchReportModule(
            ReportStyleConfig style,
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

        PdfTableBuilderInterface<BatchRegResponseDto> table = new PdfTableBuilder<>(style, mapper);
        ReportBuilder<BatchRegResponseDto> builder = new PdfReportBuilder<>(header, footer, table);

        return new ReportModule<>(mapper, table, builder);
    }

    @Bean
    public ReportModule<AdminUserDto> adminReportModule(
            ReportStyleConfig style,
            PdfHeaderBuilder header,
            PdfFooterBuilder footer
    ) {
        RowMapper<AdminUserDto> mapper = user -> List.of(
                user.getId().toString(),
                user.getFullName(),
                user.getUserName(),
                user.getEmail(),
                user.getRole(),
                user.isVerified() ? "Sí" : "No",
                user.isAccountLocked() ? "Sí" : "No",
                user.isUserLocked() ? "Sí" : "No"
        );

        PdfTableBuilderInterface<AdminUserDto> table = new PdfTableBuilder<>(style, mapper);
        ReportBuilder<AdminUserDto> builder = new PdfReportBuilder<>(header, footer, table);

        return new ReportModule<>(mapper, table, builder);
    }

    @Bean
    public PdfHeaderBuilder pdfHeaderBuilder(UniversityConfig university) {
        return new PdfHeaderBuilder(university);
    }

    @Bean
    public PdfFooterBuilder pdfFooterBuilder(ReportStyleConfig style) {
        return new PdfFooterBuilder(style);
    }


    @Bean
    public CommandLineRunner registerReportModules(
            ReportRegistry registry,
            ReportModule<BatchRegResponseDto> batchModule,
            ReportModule<AdminUserDto> adminModule
    ) {
        return args -> {
            registry.register(BatchRegResponseDto.class, batchModule.getReportBuilder());
            registry.register(AdminUserDto.class, adminModule.getReportBuilder());
        };
    }
}
