package com.tesisUrbe.backend.users.controllers;

import com.tesisUrbe.backend.auth.services.AuthService;
import com.tesisUrbe.backend.common.exception.ApiErrorFactory;
import com.tesisUrbe.backend.common.exception.ApiResponse;
import com.tesisUrbe.backend.users.dto.AdminUserDto;
import com.tesisUrbe.backend.users.dto.NewAdminUserDto;
import com.tesisUrbe.backend.users.dto.PublicUserDto;
import com.tesisUrbe.backend.users.services.UserService;
import com.tesisUrbe.backend.users.dto.NewPublicUserDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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
            @RequestParam(defaultValue = "userName") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false) String search
    ) {
        ApiResponse<Page<AdminUserDto>> response = userService.getAdminAllUsers(page, size, sortBy, sortDir, search);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

//    @PutMapping("/{id}/unlock")
//    @PreAuthorize("hasAnyRole('ADMIN','SUPERUSER')")
//    public ResponseEntity<ApiResponse<Void>> unlockUser(@PathVariable Long id) {
//        ApiResponse<Void> response = userService.unlockUserAccount(id);
//        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
//    }

    @DeleteMapping("admin/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPERUSER')")
    public ResponseEntity<ApiResponse<Void>> softDeleteUser(@PathVariable Long id) {
        ApiResponse<Void> response = userService.softDeleteUser(id);
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


}