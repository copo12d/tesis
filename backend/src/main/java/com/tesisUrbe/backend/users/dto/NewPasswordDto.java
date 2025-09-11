package com.tesisUrbe.backend.users.dto;

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
