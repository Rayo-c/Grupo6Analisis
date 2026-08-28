package com.quickbite.quejas.controller;

import com.quickbite.quejas.dto.reporte.ReporteQuejasResponse;
import com.quickbite.quejas.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/** CU15 - Generar Reportes de Quejas. Acceso ROLE_SUPERVISOR / ROLE_ADMIN (ver SecurityConfig). */
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    public ReporteQuejasResponse generar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Long sucursalId,
            @RequestParam(required = false) Long categoriaId) {
        return reporteService.generar(desde, hasta, sucursalId, categoriaId);
    }
}
