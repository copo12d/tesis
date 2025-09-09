package com.tesisUrbe.backend.users.controllers;

import com.tesisUrbe.backend.auth.services.AuthService;
import com.tesisUrbeTemp2.backend.common.exception.ApiErrorFactory;
import com.tesisUrbeTemp2.backend.common.exception.ApiResponse;
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

@RestController
@RequestMapping("${api.base-path}/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private ApiErrorFactory errorFactory;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

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
    @PreAuthorize("hasAnyRole('ADMIN','SUPERUSER')")
    public ResponseEntity<ApiResponse<AdminUserDto>> getAdminUserById(@PathVariable Long id) {
        ApiResponse<AdminUserDto> response = userService.getAdminUserById(id);
        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERUSER')")
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
//
//    @DeleteMapping("/{id}/soft-delete")
//    @PreAuthorize("hasAnyRole('ADMIN','SUPERUSER')")
//    public ResponseEntity<ApiResponse<Void>> softDeleteUser(@PathVariable Long id) {
//        ApiResponse<Void> response = userService.softDeleteUser(id);
//        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
//    }
//
//    @PutMapping("/{id}/public")
//    @PreAuthorize("#id == principal.id")
//    public ResponseEntity<ApiResponse<Void>> updatePublicUser(
//            @PathVariable Long id,
//            @Valid @RequestBody UpdatePublicUserDto dto) {
//        ApiResponse<Void> response = userService.updatePublicUser(id, dto);
//        return ResponseEntity.status(HttpStatus.valueOf(response.meta().status())).body(response);
//    }
//

}