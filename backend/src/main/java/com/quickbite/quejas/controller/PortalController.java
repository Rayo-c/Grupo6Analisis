package com.quickbite.quejas.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** CU00 - Portal (Pagina de Inicio). Contenido institucional publico. */
@RestController
@RequestMapping("/api/portal")
public class PortalController {


    private static final String MISION_DEFAULT = "Brindar a los clientes de QuickBite un canal transparente, rapido y confiable para expresar sus quejas y ver como se resuelven.";
    private static final String VISION_DEFAULT = "Ser el estandar de atencion al cliente en la industria de comida rapida a traves de la mejora continua basada en la voz del cliente.";
    private static final String VALORES_DEFAULT = "Transparencia, Rapidez, Empatia, Mejora Continua";

    private volatile String mision = MISION_DEFAULT;
    private volatile String vision = VISION_DEFAULT;
    private volatile String valores = VALORES_DEFAULT;

    @GetMapping("/info")
    public Map<String, String> info() {
        return Map.of("mision", mision, "vision", vision, "valores", valores);
    }

    @PutMapping("/info")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Map<String, String> actualizar(@RequestBody Map<String, String> body) {
        if (body.get("mision") != null && !body.get("mision").isBlank()) mision = body.get("mision").trim();
        if (body.get("vision") != null && !body.get("vision").isBlank()) vision = body.get("vision").trim();
        if (body.get("valores") != null && !body.get("valores").isBlank()) valores = body.get("valores").trim();
        return info();
    }

    @PostMapping("/info/restaurar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Map<String, String> restaurar() {
        mision = MISION_DEFAULT;
        vision = VISION_DEFAULT;
        valores = VALORES_DEFAULT;
        return info();
    }
}

    /**
    @GetMapping("/info")
    public Map<String, String> info() {
        return Map.of(
                "mision", "Brindar a los clientes de QuickBite un canal transparente, rapido y confiable para expresar sus quejas y ver como se resuelven.",
                "vision", "Ser el estandar de atencion al cliente en la industria de comida rapida a traves de la mejora continua basada en la voz del cliente.",
                "valores", "Transparencia, Rapidez, Empatia, Mejora Continua"
        );
    }
}
     **/


