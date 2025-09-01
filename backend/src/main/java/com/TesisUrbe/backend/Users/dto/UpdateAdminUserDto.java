package com.tesisUrbe.backend.users.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAdminUserDto {
    private String userName;
    private String email;
    private String password;
    private Boolean isActive;
    private Boolean isVerified;
    private Boolean isBlocked;
    private RoleDto role;
}
