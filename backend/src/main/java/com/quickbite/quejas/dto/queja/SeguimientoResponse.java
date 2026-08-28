package com.quickbite.quejas.dto.queja;

import java.time.LocalDateTime;

public record SeguimientoResponse(
        Long id,
        String usuarioNombre,
        String comentario,
        String evidenciaUrl,
        LocalDateTime fecha
) {}
