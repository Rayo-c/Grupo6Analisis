package com.quickbite.quejas.dto.queja;

import jakarta.validation.constraints.NotBlank;

/** CU10 - Dar Seguimiento a Queja. */
public record SeguimientoRequest(
        @NotBlank(message = "Debe ingresar un comentario antes de continuar.") String comentario,
        String evidenciaUrl
) {}
