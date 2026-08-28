package com.quickbite.quejas.dto.queja;

import com.quickbite.quejas.model.EstadoQueja;
import com.quickbite.quejas.model.NivelEscalamiento;
import com.quickbite.quejas.model.TipoResolucion;

import java.time.LocalDateTime;
import java.util.List;

public record QuejaResponse(
        Long id,
        String numeroSeguimiento,
        String clienteNombre,
        String clienteCorreo,
        boolean esInvitado,
        String sucursalNombre,
        String categoriaNombre,
        LocalDateTime fechaHoraIncidente,
        String descripcion,
        String evidenciaUrl,
        EstadoQueja estado,
        String agenteAsignadoNombre,
        Integer numeroResolucion,
        String solucion,
        TipoResolucion tipoResolucion,
        String motivoEscalamiento,
        NivelEscalamiento nivelEscalamiento,
        Integer vecesReabierta,
        LocalDateTime fechaRegistro,
        LocalDateTime fechaResolucion,
        List<SeguimientoResponse> historial,
        List<CalificacionResponse> calificaciones
) {}
