package com.tesisUrbe.backend.usersManagement.controllers;

import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.usersManagement.dto.*;
import com.tesisUrbe.backend.usersManagement.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("${api.base-path}/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/public/register")
    public ResponseEntity<ApiResponse<Void>> registerUser(@Valid @RequestBody NewPublicUserDto newPublicUserDto) {
        ApiResponse<Void> response = userService.registerPublicUser(newPublicUserDto);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @PostMapping("/admin/register")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Void>> registerAdmin(@Valid @RequestBody NewAdminUserDto newAdminUserDto) {
        ApiResponse<Void> response = userService.registerAdminUser(newAdminUserDto);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<ApiResponse<PublicUserDto>> getPublicUserById(@PathVariable Long id) {
        ApiResponse<PublicUserDto> response = userService.getPublicUserById(id);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/public/idByUsername/{username}")
    public ResponseEntity<ApiResponse<Long>> getIdByUsername(@PathVariable String username) {
        ApiResponse<Long> response = userService.getIdByUserName(username);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<AdminUserDto>> getAdminUserById(@PathVariable Long id) {
        ApiResponse<AdminUserDto> response = userService.getAdminUserById(id);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Page<AdminUserDto>>> getAdminAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false) String search
    ) {
        ApiResponse<Page<AdminUserDto>> response = userService.getAdminAllUsers(page, size, sortBy, sortDir, search);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/admin/search")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Page<AdminUserDto>>> searchUsersAdvanced(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(required = false) Boolean accountLocked,
            @RequestParam(required = false) Boolean userLocked,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        ApiResponse<Page<AdminUserDto>> response = userService.searchAdvanced(
                searchTerm, role, verified, accountLocked, userLocked, pageable
        );

        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @PutMapping("/public/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Void>> updatePublicUser(
            @PathVariable Long id,
            @RequestBody UpdatePublicUserDto updateUserDto) {
        ApiResponse<Void> response = userService.updatePublicUser(id, updateUserDto);
        return ResponseEntity.status(response.meta().status()).body(response);
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Void>> updateAdminUser(
            @PathVariable Long id,
            @RequestBody UpdateAdminUserDto updateUserDto) {
        ApiResponse<Void> response = userService.updateAdminUser(id, updateUserDto);
        return ResponseEntity.status(response.meta().status()).body(response);
    }

    @DeleteMapping("admin/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Void>> softDeleteUser(@PathVariable Long id) {
        ApiResponse<Void> response = userService.softDeleteUser(id);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }
}