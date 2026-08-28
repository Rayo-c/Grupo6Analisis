package com.quickbite.quejas.controller;

import com.quickbite.quejas.dto.sucursal.SucursalRequest;
import com.quickbite.quejas.dto.sucursal.SucursalResponse;
import com.quickbite.quejas.service.SucursalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** CU05 - Administrar Sucursales. Escritura exclusiva ROLE_ADMIN (ver SecurityConfig). */
@RestController
@RequestMapping("/api/sucursales")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService sucursalService;

    @GetMapping
    public List<SucursalResponse> listar() {
        return sucursalService.listar();
    }

    @PostMapping
    public SucursalResponse crear(@Valid @RequestBody SucursalRequest request) {
        return sucursalService.crear(request);
    }

    @PutMapping("/{id}")
    public SucursalResponse actualizar(@PathVariable Long id, @Valid @RequestBody SucursalRequest request) {
        return sucursalService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public SucursalResponse cambiarEstado(@PathVariable Long id) {
        return sucursalService.cambiarEstado(id);
    }
}
