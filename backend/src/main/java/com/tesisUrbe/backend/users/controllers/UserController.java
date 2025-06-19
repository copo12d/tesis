package com.tesisUrbe.backend.users.controllers;

import com.tesisUrbe.backend.auth.services.AuthService;
import com.tesisUrbe.backend.users.services.UserService;
import com.tesisUrbe.backend.users.dto.NewUserDto;
import com.tesisUrbe.backend.users.exceptions.UserAlreadyExistsException;
import com.tesisUrbe.backend.auth.controllers.AuthController;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody NewUserDto newUserDto, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(authService.errorMap(bindingResult));
        }
        try {
            userService.registerUser(newUserDto, authentication);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Usuario registrado con éxito"));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

}
