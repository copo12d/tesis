package com.tesisUrbe.backend.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRecovery {
    private String userName;
    private String email;
}
