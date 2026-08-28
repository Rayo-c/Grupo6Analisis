package com.quickbite.quejas.dto.categoria;

import com.quickbite.quejas.model.EstadoGeneral;

public record CategoriaResponse(
        Long id,
        String nombre,
        String descripcion,
        Integer slaHoras,
        EstadoGeneral estado
) {}
