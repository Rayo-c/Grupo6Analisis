package com.quickbite.quejas.dto.queja;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/** CU07 - Registrar Queja. Si el cliente no esta autenticado, se usan los campos de invitado. */
public record QuejaRequest(
        @NotNull(message = "El campo es obligatorio.") Long sucursalId,
        @NotNull(message = "El campo es obligatorio.") Long categoriaId,
        @NotNull(message = "El campo es obligatorio.") LocalDateTime fechaHoraIncidente,
        @NotBlank(message = "El campo es obligatorio.") String descripcion,
        String evidenciaUrl,
        // Solo si se registra como invitado (FA01):
        String invitadoNombre,
        String invitadoCorreo,
        String invitadoTelefono
) {}
