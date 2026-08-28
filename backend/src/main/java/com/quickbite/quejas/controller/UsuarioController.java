package com.quickbite.quejas.controller;

import com.quickbite.quejas.dto.usuario.CambiarEstadoRequest;
import com.quickbite.quejas.dto.usuario.UsuarioPersonalRequest;
import com.quickbite.quejas.dto.usuario.UsuarioResponse;
import com.quickbite.quejas.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** CU04 - Administrar Cuentas de Usuario. Acceso exclusivo ROLE_ADMIN (ver SecurityConfig). */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/personal")
    public List<UsuarioResponse> listarPersonal(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long sucursalId) {
        return usuarioService.listarPersonal(texto, rol, estado, sucursalId);
    }

    @PostMapping("/personal")
    public UsuarioResponse crearPersonal(@Valid @RequestBody UsuarioPersonalRequest request) {
        return usuarioService.crearPersonal(request);
    }

    @PutMapping("/personal/{id}")
    public UsuarioResponse actualizarPersonal(@PathVariable Long id, @Valid @RequestBody UsuarioPersonalRequest request) {
        return usuarioService.actualizarPersonal(id, request);
    }

    @PatchMapping("/personal/{id}/estado")
    public UsuarioResponse cambiarEstadoPersonal(@PathVariable Long id) {
        return usuarioService.cambiarEstadoPersonal(id);
    }

    @GetMapping("/clientes")
    public List<UsuarioResponse> listarClientes(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) String estado) {
        return usuarioService.listarClientes(texto, estado);
    }

    @PatchMapping("/clientes/{id}/estado")
    public UsuarioResponse cambiarEstadoCliente(@PathVariable Long id, @RequestBody CambiarEstadoRequest request) {
        return usuarioService.cambiarEstadoCliente(id, request);
    }
}
