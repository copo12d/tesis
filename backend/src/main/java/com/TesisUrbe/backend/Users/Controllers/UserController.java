package com.tesisUrbe.backend.users.controllers;

import com.tesisUrbe.backend.auth.services.AuthService;
import com.tesisUrbe.backend.users.dto.UpdateAdminUserDto;
import com.tesisUrbe.backend.users.dto.UpdatePublicUserDto;
import com.tesisUrbe.backend.users.dto.UserDto;
import com.tesisUrbe.backend.users.exceptions.InvalidUserDataException;
import com.tesisUrbe.backend.users.exceptions.RoleNotFoundException;
import com.tesisUrbe.backend.users.services.UserService;
import com.tesisUrbe.backend.users.dto.NewUserDto;
import com.tesisUrbe.backend.users.exceptions.UserAlreadyExistsException;
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
            return ResponseEntity
                    .badRequest()
                    .body(authService.errorMap(bindingResult));
        }
        try {
            userService.registerPublicUser(newUserDto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("message", "Usuario registrado exitosamente"));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERUSER')")
    @PutMapping("/admin/register")
    public ResponseEntity<Map<String, String>> registerAdminUser(
            @Valid @RequestBody NewUserDto newUserDto,
            BindingResult bindingResult,
            Authentication authentication
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(authService.errorMap(bindingResult));
        }
        try {
            userService.registerAdminUser(newUserDto, authentication);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("message", "Usuario registrado exitosamente"));
        } catch (UserAlreadyExistsException | RoleNotFoundException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN','SUPERUSER')")
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getPublicUserById(
            @PathVariable("id") Long userId,
            Authentication authentication
    ) {
        try {
            UserDto userDto = userService.findPublicUserById(userId, authentication);
            return ResponseEntity.ok(userDto);
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERUSER')")
    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getAdminUserById(
            @PathVariable("id") Long userId,
            Authentication authentication
    ) {
        try {
            UserDto userDto = userService.findAdminUserById(userId, authentication);
            return ResponseEntity.ok(userDto);
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERUSER')")
    @GetMapping("/admin")
    public ResponseEntity<?> getAllAdminUsers(Authentication authentication) {
        try {
            List<UserDto> users = userService.findAllAdminUsers(authentication);
            return ResponseEntity.ok(users);
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<Map<String, String>> updatePublicUser(
            @PathVariable("id") Long userId,
            @Valid @RequestBody UpdatePublicUserDto updateUserDto,
            BindingResult bindingResult,
            Authentication authentication
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(authService.errorMap(bindingResult));
        }
        try {
            userService.updatePublicUser(userId, updateUserDto, authentication);
            return ResponseEntity.ok(Map.of("message", "Usuario actualizado exitosamente"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (InvalidUserDataException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno"));
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERUSER')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<Map<String, String>> updateAdminUser(
            @PathVariable("id") Long userId,
            @Valid @RequestBody UpdateAdminUserDto updateUserDto,
            BindingResult bindingResult,
            Authentication authentication
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(authService.errorMap(bindingResult));
        }

        try {
            userService.updateAdminUser(userId, updateUserDto, authentication);
            return ResponseEntity
                    .ok(Map.of("message", "Usuario actualizado exitosamente"));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (RoleNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (InvalidUserDataException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERUSER')")
    @PutMapping("/admin/{id}/unlock")
    public ResponseEntity<?> unlockUserAccount(
            @PathVariable("id") Long userId,
            Authentication authentication
    ) {
        try {
            userService.unlockUserAccount(userId, authentication);
            return ResponseEntity.ok(Map.of("message", "Usuario desbloqueado exitosamente"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERUSER')")
    @DeleteMapping("/admin/{userId}/delete")
    public ResponseEntity<Map<String, String>> softDeleteUser(
            @PathVariable Long userId,
            Authentication authentication
    ) {
        try {
            userService.softDeleteUser(userId, authentication);
            return ResponseEntity
                    .ok(Map.of("message", "Usuario eliminado con éxito"));
        } catch (UsernameNotFoundException | IllegalArgumentException | AccessDeniedException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

}
