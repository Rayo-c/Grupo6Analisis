package com.quickbite.quejas.dto.reporte;

import java.util.List;
import java.util.Map;

/** CU15 - Generar Reportes de Quejas. */
public record ReporteQuejasResponse(
        long totalQuejas,
        Map<String, Long> porCategoria,
        Map<String, Long> porSucursal,
        Map<String, Long> porEstado,
        Double tiempoPromedioResolucionHoras,
        Double indiceSatisfaccionPromedio,
        List<AgenteDesempenoResponse> desempenoPorAgente
) {}
