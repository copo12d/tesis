package com.tesisUrbe.backend.reportsManagerPdf.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "university")
@Data
public class UniversityConfig {
    private String legalName;
    private TaxId taxId;
    private String address_1;
    private String address_2;
    private String address_3;
    private String phone;
    private String email;
    private String logoPath;

    @Data
    public static class TaxId {
        private String type;
        private String number;
    }
}

