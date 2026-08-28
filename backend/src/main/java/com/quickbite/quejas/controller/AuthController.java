package com.quickbite.quejas.controller;

import com.quickbite.quejas.dto.auth.*;
import com.quickbite.quejas.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** CU01 Iniciar Sesion, CU02 Registrar Cliente, CU03 Recuperar Contrasena. */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/registro")
    public ResponseEntity<Void> registrarCliente(@Valid @RequestBody RegistroClienteRequest request) {
        authService.registrarCliente(request);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/verificar/{token}")
    public ResponseEntity<Void> verificarCuenta(@PathVariable String token) {
        authService.verificarCuenta(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<Void> recuperarPassword(@Valid @RequestBody RecuperarPasswordRequest request) {
        authService.recuperarPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}
