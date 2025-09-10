package com.tesisUrbe.backend.users.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePublicUserDto {
    private String userName;
    private String email;
    private String password;
}

