package com.quickbite.quejas.dto.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CategoriaRequest(
        @NotBlank(message = "El campo es obligatorio.") String nombre,
        String descripcion,
        @NotNull(message = "El campo es obligatorio.") @Positive(message = "El campo es obligatorio.") Integer slaHoras
) {}
