package com.tesisUrbe.backend.solidWasteManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewContainerTypeDto {
    private String name;
    private String description;
}
