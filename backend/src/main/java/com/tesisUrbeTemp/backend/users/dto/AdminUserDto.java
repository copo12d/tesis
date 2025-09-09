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
<<<<<<<< HEAD:backend/src/main/java/com/tesisUrbeTemp/backend/users/dto/UserDto.java
    private boolean isActive;
    private boolean isVerified;
    private boolean isBlocked;
========
    private boolean active;
    private boolean verified;
    private boolean accountLocked;
    private boolean userLocked;
    private boolean deleted;
>>>>>>>> b4dcb59 (tesis refactorizada.):backend/src/main/java/com/tesisUrbeTemp/backend/users/dto/AdminUserDto.java
}

