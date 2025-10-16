package com.tesisUrbe.backend.prediction.dto;

import java.time.DayOfWeek;
import java.time.Month;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrReportResponseDto {

    private String containerSerial;
    private String reporterIp;
    private DayOfWeek dayOfWeek;
    private Month month;
    private String reportDate;
    private String reportTime;
    private String cycleStatus;
    
}
