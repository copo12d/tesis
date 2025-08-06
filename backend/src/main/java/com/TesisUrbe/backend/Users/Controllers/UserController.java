package com.tesisUrbe.backend.users.controllers;

import com.tesisUrbe.backend.auth.services.AuthService;
import com.tesisUrbe.backend.users.dto.RoleUpdateDto;
import com.tesisUrbe.backend.users.dto.UpdateUserDto;
import com.tesisUrbe.backend.users.enums.RoleList;
import com.tesisUrbe.backend.users.exceptions.RoleNotFoundException;
import com.tesisUrbe.backend.users.model.Role;
import com.tesisUrbe.backend.users.model.User;
import com.tesisUrbe.backend.users.services.RoleService;
import com.tesisUrbe.backend.users.services.UserService;
import com.tesisUrbe.backend.users.dto.NewUserDto;
import com.tesisUrbe.backend.users.exceptions.UserAlreadyExistsException;
import com.tesisUrbe.backend.auth.controllers.AuthController;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerPublicUser(
            @Valid @RequestBody NewUserDto newUserDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(authService.errorMap(bindingResult));
        }
        try {
            userService.registerUser(newUserDto, null); // Sin Authentication
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Usuario registrado exitosamente"));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERUSER')")
    public ResponseEntity<Map<String, String>> createPrivilegedUser(
            @Valid @RequestBody NewUserDto newUserDto,
            BindingResult bindingResult,
            Authentication authentication
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(authService.errorMap(bindingResult));
        }

        try {
            userService.registerUser(newUserDto, authentication);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Usuario con rol [" + newUserDto.getRole() + "] registrado exitosamente"));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            return ResponseEntity.ok(user);
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(Authentication authentication) {
        try {
            List<User> users = userService.findAll(authentication);
            return ResponseEntity.ok(users);
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener los usuarios");
        }
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<?> getUserStatus(@PathVariable Long id) {
        try {
            boolean isActive = userService.isActive(id);
            boolean isBlocked = userService.isBlocked(id);
            boolean isVerified = userService.isVerified(id);

            return ResponseEntity.ok(Map.of(
                    "activo", isActive,
                    "bloqueado", isBlocked,
                    "verificado", isVerified
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al obtener el estado del usuario"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserDto updateUserDto, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(authService.errorMap(bindingResult));
        }
        try {
            userService.updateUser(id, updateUserDto, authentication);
            return ResponseEntity.ok(Map.of("message", "Usuario actualizado con éxito"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno"));
        }
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long id) {
        try {
            userService.blockUser(id);
            return ResponseEntity.ok(Map.of("message", "Usuario bloqueado con éxito"));
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno"));
        }
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateDto roleUpdateDto,
            BindingResult bindingResult,
            Authentication authentication
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(authService.errorMap(bindingResult));
        }

        try {
            userService.updateRoleById(id, roleUpdateDto, authentication);
            return ResponseEntity.ok(Map.of("message", "Rol actualizado con éxito"));
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        } catch (RoleNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno: " + ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable Long id, Authentication authentication) {
        try {
            userService.deactivateUserById(id, authentication);
            return ResponseEntity.ok("Usuario desactivado correctamente");
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desactivar el usuario");
        }
    }

}
