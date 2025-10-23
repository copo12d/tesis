package com.tesisUrbe.backend.solidWasteManagement.dto;

import java.time.DayOfWeek;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TotalWeightResponseDto {


    DayOfWeek totalWeightOfTheDay;
}
