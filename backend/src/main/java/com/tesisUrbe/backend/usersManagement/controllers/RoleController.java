package com.tesisUrbe.backend.usersManagement.controllers;

import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.usersManagement.dto.RoleRespondDto;
import com.tesisUrbe.backend.usersManagement.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base-path}/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final ApiErrorFactory errorFactory;

    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<List<RoleRespondDto>>> getVisibleRoles() {
        List<RoleRespondDto> roles = roleService.getVisibleRolesForCurrentUser();
        return ResponseEntity.ok(new ApiResponse<>(
                errorFactory.buildMeta(HttpStatus.OK, "Roles visibles obtenidos correctamente"),
                roles,
                null
        ));
    }
}
