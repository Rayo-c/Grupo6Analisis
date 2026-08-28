package com.quickbite.quejas.dto.usuario;

import com.quickbite.quejas.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** CU04 - Crear/editar cuenta de personal (Agente, Supervisor, Administrador). */
public record UsuarioPersonalRequest(
        @NotBlank(message = "El campo es obligatorio.") String nombreCompleto,
        @NotBlank(message = "El campo es obligatorio.") @Email(message = "El formato del correo electronico no es valido.") String correo,
        @NotNull(message = "El campo es obligatorio.") Rol rol,
        Long sucursalId
) {}
