package com.quickbite.quejas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** CU00 - Portal (Pagina de Inicio). Contenido institucional publico. */
@RestController
@RequestMapping("/api/portal")
public class PortalController {

    @GetMapping("/info")
    public Map<String, String> info() {
        return Map.of(
                "mision", "Brindar a los clientes de QuickBite un canal transparente, rapido y confiable para expresar sus quejas y ver como se resuelven.",
                "vision", "Ser el estandar de atencion al cliente en la industria de comida rapida a traves de la mejora continua basada en la voz del cliente.",
                "valores", "Transparencia, Rapidez, Empatia, Mejora Continua"
        );
    }
}
