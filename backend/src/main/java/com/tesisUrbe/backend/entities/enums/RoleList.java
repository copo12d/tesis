package com.tesisUrbe.backend.entities.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleList {
    ROLE_USER("Usuario"),
    ROLE_EMPLOYEE("Empleado"),
    ROLE_ADMIN("Administrador"),
    ROLE_SUPERUSER("Super Usuario");

    private final String description;
}
