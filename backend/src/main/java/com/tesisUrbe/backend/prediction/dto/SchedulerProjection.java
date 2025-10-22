package com.tesisUrbe.backend.prediction.dto;

import com.tesisUrbe.backend.entities.solidWaste.Container;

import java.time.LocalDateTime;

public interface SchedulerProjection {

    Container getContainer();
    LocalDateTime getSchedulerFillTime();
    Long getId();
}
