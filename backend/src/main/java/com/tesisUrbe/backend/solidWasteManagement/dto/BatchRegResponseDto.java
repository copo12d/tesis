package com.tesisUrbe.backend.solidWasteManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
<<<<<<<< HEAD:backend/src/main/java/com/tesisUrbe/backend/solidWasteManagement/dto/WasteResponseDto.java
@AllArgsConstructor
public class WasteResponseDto {
    private Long id;
    private BigDecimal weight;
    private LocalDateTime collectionDate;
    private Long containerId;
    private Long batchId;
========
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchRegResponseDto {
    private String serial;
    private BigDecimal weight;
    private String createdByUsername;
    private String date;
    private String hour;
>>>>>>>> origin/angel-branch:backend/src/main/java/com/tesisUrbe/backend/solidWasteManagement/dto/BatchRegResponseDto.java
}
