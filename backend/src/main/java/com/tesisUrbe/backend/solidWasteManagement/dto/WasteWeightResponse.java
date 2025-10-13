package com.tesisUrbe.backend.solidWasteManagement.dto;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.Year;
import java.util.Map;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WasteWeightResponse {

    String containerType;
    BigDecimal weightTotal;
    Map<DayOfWeek, BigDecimal> dayWeight;
    Map<Month, BigDecimal> monthWeight;
    Map<Year, BigDecimal> yearWeight;
}
