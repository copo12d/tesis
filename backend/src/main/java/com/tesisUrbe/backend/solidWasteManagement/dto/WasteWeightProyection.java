package com.tesisUrbe.backend.solidWasteManagement.dto;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.Year;


public interface WasteWeightProyection {
    String getContainerType();  
    DayOfWeek getCollectionDayOfWeek();
    Year getCollectionYear();
    Month getCollectionMonth(); 
    BigDecimal getWeight();
}
