package com.tesisUrbe.backend.prediction.dto;

import java.time.LocalDateTime;

public interface NextRecollectionProjection {
    String getContainerSerial();

    LocalDateTime getNextRecollectionTime();

}
