package com.tesisUrbe.backend.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
<<<<<<<< HEAD:backend/src/main/java/com/tesisUrbeTemp/backend/users/dto/PublicUserDto.java
public class PublicUserDto {
========
public class AdminUserDto {
>>>>>>>> origin/luis-branch:backend/src/main/java/com/tesisUrbeTemp/backend/users/dto/AdminUserDto.java
    private Long id;
    private String fullName;
    private String userName;
    private String email;
    private boolean active;
    private boolean verified;
    private boolean accountLocked;
    private boolean userLocked;
    private boolean deleted;
}

