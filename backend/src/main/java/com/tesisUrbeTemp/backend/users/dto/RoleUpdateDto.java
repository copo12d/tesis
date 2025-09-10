package com.tesisUrbe.backend.users.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoleUpdateDto {
    @NotNull
    private String roleName;
}
