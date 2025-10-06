package com.tesisUrbe.backend.prediction.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class ManualSchedulerDto {
    String containerSerial;
    List<LocalDateTime> schedulers;
}
