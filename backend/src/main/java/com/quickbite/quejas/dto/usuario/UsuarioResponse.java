package com.quickbite.quejas.dto.usuario;

import com.quickbite.quejas.model.EstadoCuenta;
import com.quickbite.quejas.model.Rol;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nombreCompleto,
        String correo,
        String telefono,
        Rol rol,
        EstadoCuenta estado,
        Long sucursalId,
        String sucursalNombre,
        LocalDateTime fechaCreacion
) {}
