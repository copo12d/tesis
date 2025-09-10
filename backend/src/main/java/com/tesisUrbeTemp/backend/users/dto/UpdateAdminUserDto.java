package com.tesisUrbe.backend.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewPublicUserDto {
    private String userName;
    private String password;
    private String email;
}
