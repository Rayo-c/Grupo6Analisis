package com.quickbite.quejas.dto.queja;

import com.quickbite.quejas.model.TipoResolucion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** CU11 - Resolver Queja. */
public record ResolverRequest(
        @NotBlank(message = "No se puede resolver una queja sin una respuesta al cliente.") String solucion,
        @NotNull(message = "El campo es obligatorio.") TipoResolucion tipoResolucion
) {}
