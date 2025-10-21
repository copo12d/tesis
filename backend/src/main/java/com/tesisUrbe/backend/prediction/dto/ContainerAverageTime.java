package com.tesisUrbe.backend.prediction.dto;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.Map;

import com.tesisUrbe.backend.entities.solidWaste.Container;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ContainerAverageTime {
    Container container;
    Double totalAverage;
    Map<DayOfWeek, Double> dayAverage;
    Map<Month, Double> monthAverage;
    Map<Map<Month, DayOfWeek>, Double> monthDayAverage;

}
