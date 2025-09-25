package com.tesisUrbe.backend.solidWasteManagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContainerTypeRequestDto {
    @NotBlank
    private String name;

    @NotBlank
    private String description;
}
