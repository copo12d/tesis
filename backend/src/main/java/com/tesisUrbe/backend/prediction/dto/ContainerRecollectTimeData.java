package com.tesisUrbe.backend.prediction.dto;

import java.time.DayOfWeek;
import java.time.Month;

import com.tesisUrbe.backend.entities.solidWaste.Container;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ContainerRecollectTimeData {
    Container container;
    DayOfWeek dayOfWeek;
    Month month;
    Double averageTime;
    
}
