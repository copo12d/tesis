package com.tesisUrbe.backend.usersManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewAdminUserDto {
    private String fullName;
    private String userName;
    private String password;
    private String email;
    private String role;
}
