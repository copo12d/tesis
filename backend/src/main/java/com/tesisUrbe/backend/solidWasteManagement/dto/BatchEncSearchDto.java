package com.tesisUrbe.backend.solidWasteManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchEncSearchDto {
    private String description;
    private String status;
    private String fechaInicio;
    private String fechaFin;
    private int page = 0;
    private int size = 10;
    private String sortBy = "creationDate";
    private String sortDir = "DESC";
}
