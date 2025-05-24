package com.TesisUrbe.backend.security.controllers;

import com.TesisUrbe.backend.security.dto.LoginUserDto;
import com.TesisUrbe.backend.security.dto.NewUserDto;
import com.TesisUrbe.backend.security.exceptions.UserAlreadyExistsException;
import com.TesisUrbe.backend.security.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/saludo")
    public String hola(){
        return "hola";
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginUserDto loginUserDto, BindingResult bindingResult) {
        if(bindingResult.hasGlobalErrors()){
            return ResponseEntity.badRequest().body(Collections.singletonMap("error","Revise sus credenciales"));
        }
        try{
            String jwt = authService.Authenticate(loginUserDto.getUserName(), loginUserDto.getPassword());
            return ResponseEntity.ok(Collections.singletonMap("token", jwt));
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Credenciales incorrectas"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody NewUserDto newUserDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(errorMap(bindingResult));
        }
        try {
            authService.registerUser(newUserDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Usuario registrado con éxito"));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @GetMapping("/check-auth")
    public ResponseEntity<Map<String, String>> checkAuth() {
        return ResponseEntity.ok(Map.of("status", "Autenticado"));
    }

    @ExceptionHandler({org.springframework.security.access.AccessDeniedException.class, org.springframework.security.authentication.AuthenticationCredentialsNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleAuthException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autorizado"));
    }

    private Map<String, String> errorMap(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }
}
