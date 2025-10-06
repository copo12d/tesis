package com.tesisUrbe.backend.prediction.dto;

import com.tesisUrbe.backend.entities.solidWaste.Container;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class NewContainerSchedulerDto {
    Container container;
    Integer fillingNumber;
    LocalDateTime schedulerFillTime;

}
