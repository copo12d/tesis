package com.tesisUrbe.backend.solidWasteManagement.dto;

import java.math.BigDecimal;

public interface WasteWeightProyectionDTO {
    String getContainerType();
    Integer getCollectionDayOfWeek();
    Integer getCollectionYear();
    Integer getCollectionMonth();
    BigDecimal getTotalWeight();
}
