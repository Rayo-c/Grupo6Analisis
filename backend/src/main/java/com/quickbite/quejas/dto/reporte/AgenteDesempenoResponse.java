package com.quickbite.quejas.dto.reporte;

public record AgenteDesempenoResponse(
        String agenteNombre,
        long quejasAtendidas,
        Double calificacionPromedio
) {}
