package com.tesisUrbeTemp2.backend.solidWasteManagement.dto;

import com.tesisUrbeTemp2.backend.entities.solidWaste.Batch;
import com.tesisUrbeTemp2.backend.entities.solidWaste.Container;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewWasteDto {
    private BigDecimal weight;
    private LocalDateTime collectionDate;
    private Container container;
    private Batch batch;
}
