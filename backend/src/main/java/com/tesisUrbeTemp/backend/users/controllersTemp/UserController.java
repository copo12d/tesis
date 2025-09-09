package com.tesisUrbe.backend.users.controllersTemp;

import com.tesisUrbe.backend.auth.services.AuthService;
import com.tesisUrbe.backend.users.dto.*;
import com.tesisUrbe.backend.users.exceptions.*;
import com.tesisUrbe.backend.users.services.UserService;
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

    //POST
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
        }catch (InvalidUserPasswordException e){
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("password error", e.getMessage()));
        } catch (UserAlreadyExistsException | InvalidUserDataException e ) {
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
    @PostMapping("/admin/register")
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
        }catch (BlockedUserException e){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }catch (InvalidUserPasswordException e){
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("password error", e.getMessage()));
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

    //GET
    @PreAuthorize("hasAnyRole('USER','ADMIN','SUPERUSER')")
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getPublicUserById(
            @PathVariable("id") Long userId,
            Authentication authentication
    ) {
        try {
            UserDto userDto = userService.findPublicUserById(userId, authentication);
            return ResponseEntity.ok(userDto);
        } catch (AccessDeniedException | BlockedUserException e) {
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
        } catch (AccessDeniedException | BlockedUserException e) {
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
        } catch (AccessDeniedException | BlockedUserException e) {
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

    //PUT
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
        } catch (AccessDeniedException | BlockedUserException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (UserAlreadyExistsException | InvalidUserDataException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
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
        } catch (AccessDeniedException | BlockedUserException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (
                UserAlreadyExistsException |
                InvalidUserDataException |
                IllegalArgumentException e
        ) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (RoleNotFoundException | UsernameNotFoundException e) {
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
    @PutMapping("/admin/{id}/unlock")
    public ResponseEntity<?> unlockUserAccount(
            @PathVariable("id") Long userId,
            Authentication authentication
    ) {
        try {
            userService.unlockUserAccountManual(userId, authentication);
            return ResponseEntity.ok(Map.of("message", "Usuario desbloqueado exitosamente"));
        } catch (AccessDeniedException | BlockedUserException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PutMapping("/password-recovery/{id}")
    public ResponseEntity<?> passwordRecovery(
            @RequestParam(name = "token") String token,
            @PathVariable Long id,
            @Valid @RequestBody NewPasswordDto newPasswordDto,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(authService.errorMap(bindingResult));
        }
        try {
            userService.passwordRecovery(id, token, newPasswordDto.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Usuario actualizado exitosamente"));
        } catch (AccessDeniedException | BlockedUserException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (UserAlreadyExistsException | InvalidUserDataException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno"));
        }
    }

    @PutMapping("/account-recovery/{id}")
    public ResponseEntity<?> accountRecovery (
            @RequestParam(name = "token") String token,
            @PathVariable Long id,
            @Valid @RequestBody NewPasswordDto newPasswordDto,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(authService.errorMap(bindingResult));
        }
        try {
            userService.accountRecovery(id, token, newPasswordDto.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Usuario recuperado exitosamente"));
        } catch (AccessDeniedException | BlockedUserException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (UserAlreadyExistsException | InvalidUserDataException | IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno"));
        }
    }

    //DELETE
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
