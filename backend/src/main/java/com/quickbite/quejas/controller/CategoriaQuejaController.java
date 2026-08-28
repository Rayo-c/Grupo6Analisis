package com.quickbite.quejas.controller;

import com.quickbite.quejas.dto.categoria.CategoriaRequest;
import com.quickbite.quejas.dto.categoria.CategoriaResponse;
import com.quickbite.quejas.service.CategoriaQuejaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** CU06 - Administrar Categorias de Queja. Escritura exclusiva ROLE_ADMIN (ver SecurityConfig). */
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaQuejaController {

    private final CategoriaQuejaService categoriaService;

    @GetMapping
    public List<CategoriaResponse> listar() {
        return categoriaService.listar();
    }

    @PostMapping
    public CategoriaResponse crear(@Valid @RequestBody CategoriaRequest request) {
        return categoriaService.crear(request);
    }

    @PutMapping("/{id}")
    public CategoriaResponse actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return categoriaService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public CategoriaResponse cambiarEstado(@PathVariable Long id) {
        return categoriaService.cambiarEstado(id);
    }
}
