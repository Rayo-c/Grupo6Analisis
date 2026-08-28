package com.quickbite.quejas.dto.usuario;

/** CU04 - Cambiar estado de una cuenta. El motivo es obligatorio para suspender clientes. */
public record CambiarEstadoRequest(String motivo) {}
