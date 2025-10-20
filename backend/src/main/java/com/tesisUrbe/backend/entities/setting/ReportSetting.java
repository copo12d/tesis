package com.tesisUrbe.backend.entities.setting;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "report_settings")
@Data
public class ReportSetting {
    @Id
    private Long id = 1L;
    private String tableHeaderColor;
    private String headerTextColor;
    private String recordColor;
}
