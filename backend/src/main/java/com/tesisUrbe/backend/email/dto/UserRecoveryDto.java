package com.tesisUrbe.backend.email.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRecoveryDto {
    private String userName;
    private String email;
}
