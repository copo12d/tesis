package com.tesisUrbe.backend.usersManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRespondDto {

    private Long id;
    private String name;
    private String value;
}