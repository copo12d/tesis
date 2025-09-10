package com.tesisUrbe.backend.users.dto;

import com.tesisUrbe.backend.entities.enums.RoleList;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private RoleList name;
}
