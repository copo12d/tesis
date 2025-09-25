package com.tesisUrbe.backend.usersManagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePublicUserDto {
    private String fullName;
    private String userName;
    private String email;
    private String password;
}

