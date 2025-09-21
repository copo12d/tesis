package com.tesisUrbe.backend.usersManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter

public class NewPasswordDto {
    private String newPassword;
}
