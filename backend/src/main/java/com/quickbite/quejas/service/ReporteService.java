package com.quickbite.quejas.service;

import com.quickbite.quejas.dto.reporte.ReporteQuejasResponse;

import java.time.LocalDate;

/** CU15 - Generar Reportes de Quejas. */
public interface ReporteService {
    ReporteQuejasResponse generar(LocalDate desde, LocalDate hasta, Long sucursalId, Long categoriaId);
}
