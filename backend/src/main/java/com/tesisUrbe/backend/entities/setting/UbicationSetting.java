package com.tesisUrbe.backend.entities.setting;

import lombok.Data;

@Data
public class UbicationSetting {
    private double latitude;
    private double longitude;
    private int mapZoom;
}
