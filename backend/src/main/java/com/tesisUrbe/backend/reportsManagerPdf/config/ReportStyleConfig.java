package com.tesisUrbe.backend.reportsManagerPdf.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "report")
@Data
public class ReportStyleConfig {
    private String author;
    private String tableHeaderColor;
    private String headerTextColor;
    private String recordColor;
}

