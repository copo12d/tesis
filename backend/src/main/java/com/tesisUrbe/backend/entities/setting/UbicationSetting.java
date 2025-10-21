package com.tesisUrbe.backend.entities.setting;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ubication_settings")
@Data
public class UbicationSetting {
    @Id
    private Long id = 1L;

    private double latitude;
    private double longitude;
    private int mapZoom;
}
