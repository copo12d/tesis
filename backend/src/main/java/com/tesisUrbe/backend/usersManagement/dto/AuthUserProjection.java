package com.tesisUrbe.backend.usersManagement.dto;

public interface AuthUserProjection {
    String getFullName();

    String getUserName();

    String getPassword();

    String getRoleName();

    boolean isVerified();

    boolean isAccountLocked();

    boolean isUserLocked();

    boolean isDeleted();
}
