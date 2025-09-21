package com.tesisUrbe.backend.usersManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicUserDto {
    private Long id;
    private String fullName;
    private String userName;
    private String email;
    private boolean verified;
    private boolean accountLocked;
    private boolean userLocked;
    private boolean deleted;
}
