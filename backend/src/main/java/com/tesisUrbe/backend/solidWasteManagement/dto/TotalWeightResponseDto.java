package com.tesisUrbe.backend.solidWasteManagement.dto;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.Year;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TotalWeightResponseDto {

    
    DayOfWeek totalWeightOfTheDay;
}
