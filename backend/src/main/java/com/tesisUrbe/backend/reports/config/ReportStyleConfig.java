package com.tesisUrbe.backend.reports.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "report")
@Data
public class ReportStyleConfig {
    private String author;
    private String headerColor;
    private String recordColor;
    private String titleColor;
}

