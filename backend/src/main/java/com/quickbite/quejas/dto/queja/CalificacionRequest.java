package com.quickbite.quejas.dto.queja;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** CU14 - Calificar Resolucion de Queja. RN08. */
public record CalificacionRequest(
        @NotNull(message = "El campo es obligatorio.") @Min(1) @Max(5) Integer calificacion,
        String comentario
) {}
