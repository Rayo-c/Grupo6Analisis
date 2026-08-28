package com.quickbite.quejas.dto.sucursal;

import com.quickbite.quejas.model.EstadoGeneral;

public record SucursalResponse(
        Long id,
        String codigo,
        String nombre,
        String direccion,
        String telefono,
        Long supervisorId,
        String supervisorNombre,
        EstadoGeneral estado
) {}
