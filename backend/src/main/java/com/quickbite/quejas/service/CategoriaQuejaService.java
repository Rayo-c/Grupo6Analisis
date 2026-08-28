package com.quickbite.quejas.service;

import com.quickbite.quejas.dto.categoria.CategoriaRequest;
import com.quickbite.quejas.dto.categoria.CategoriaResponse;

import java.util.List;

/** CU06 - Administrar Categorias de Queja. RN03. */
public interface CategoriaQuejaService {
    List<CategoriaResponse> listar();
    CategoriaResponse crear(CategoriaRequest request);
    CategoriaResponse actualizar(Long id, CategoriaRequest request);
    CategoriaResponse cambiarEstado(Long id);
}
