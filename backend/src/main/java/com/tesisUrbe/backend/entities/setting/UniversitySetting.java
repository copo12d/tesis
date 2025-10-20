package com.tesisUrbe.backend.entities.setting;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "university_settings")
@Data
public class UniversitySetting {
    @Id
    private Long id = 1L;

    private String legalName;

    @Embedded
    private TaxId taxId;

    private String address1;
    private String address2;
    private String address3;

    private String phone;
    private String email;
    private String logoPath;

    @Embeddable
    @Data
    public static class TaxId {
        private String type;
        private String number;
    }
}
