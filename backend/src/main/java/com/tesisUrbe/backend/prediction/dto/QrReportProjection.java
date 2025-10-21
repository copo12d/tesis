package com.tesisUrbe.backend.prediction.dto;

import java.time.LocalDateTime;

public interface QrReportProjection {
    String getContainerSerial();

    String getReporterIp();

    LocalDateTime getReportTime();

    boolean getDeleteCycle();
}
