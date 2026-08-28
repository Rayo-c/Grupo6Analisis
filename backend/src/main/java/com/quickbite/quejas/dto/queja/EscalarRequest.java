package com.quickbite.quejas.dto.queja;

import com.quickbite.quejas.model.NivelEscalamiento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** CU12 - Escalar Queja. */
public record EscalarRequest(
        @NotBlank(message = "El campo es obligatorio.") String motivo,
        @NotNull(message = "El campo es obligatorio.") NivelEscalamiento nivel
) {}
