package com.tesisUrbe.backend.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDto {
    private Long id;
    private String fullName;
    private String userName;
    private String email;
    private String role;
    private boolean verified;
    private boolean accountLocked;
    private boolean userLocked;
    private boolean deleted;
}

