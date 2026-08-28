package com.quickbite.quejas.dto.queja;

import jakarta.validation.constraints.NotBlank;

/** CU13 - Reabrir Queja. */
public record ReabrirRequest(
        @NotBlank(message = "El campo es obligatorio.") String motivo
) {}
