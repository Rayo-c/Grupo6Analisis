package com.quickbite.quejas.dto.auth;

public record LoginResponse(
        String token,
        Long usuarioId,
        String nombreCompleto,
        String correo,
        String rol
) {}
