package com.tesisUrbe.backend.prediction.dto;

import java.time.DayOfWeek;
import java.time.Month;

import com.tesisUrbe.backend.entities.solidWaste.Container;

public interface ContainerRecollectTimeProyection {
    Container getContainer();
    DayOfWeek getDayOfWeek();
    Month getMonth();
    Double getAverageTime();
    
}
