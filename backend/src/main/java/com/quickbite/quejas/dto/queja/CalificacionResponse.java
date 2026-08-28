package com.quickbite.quejas.dto.queja;

import java.time.LocalDateTime;

public record CalificacionResponse(
        Integer numeroResolucion,
        Integer calificacion,
        String comentario,
        LocalDateTime fecha
) {}
