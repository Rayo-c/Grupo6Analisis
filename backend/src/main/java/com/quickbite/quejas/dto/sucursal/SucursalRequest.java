package com.quickbite.quejas.dto.sucursal;

import jakarta.validation.constraints.NotBlank;

public record SucursalRequest(
        @NotBlank(message = "El campo es obligatorio.") String codigo,
        @NotBlank(message = "El campo es obligatorio.") String nombre,
        @NotBlank(message = "El campo es obligatorio.") String direccion,
        String telefono,
        Long supervisorId
) {}
