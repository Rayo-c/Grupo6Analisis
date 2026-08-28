package com.quickbite.quejas.dto.queja;

import jakarta.validation.constraints.NotNull;

/** CU09 - FA03: reasignacion manual por el Supervisor. */
public record ReasignarRequest(
        @NotNull(message = "El campo es obligatorio.") Long nuevoAgenteId
) {}
