package com.quickbite.quejas;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * Sistema de Gestion de Quejas - QuickBite.
 * Cubre CU00 (Portal) a CU16 (Comunicacion Web Service).
 */
@SpringBootApplication
public class QuejasApplication {

    /** Zona horaria del negocio. Azure ejecuta la JVM en UTC, lo que desfasaba 6 horas las fechas. */
    public static final String ZONA_HORARIA = "America/Guatemala";

    @PostConstruct
    void configurarZonaHoraria() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZONA_HORARIA));
    }

    public static void main(String[] args) {
        // Debe fijarse antes de iniciar Spring (los @Builder.Default con LocalDateTime.now() usan la zona por defecto).
        TimeZone.setDefault(TimeZone.getTimeZone(ZONA_HORARIA));
        SpringApplication.run(QuejasApplication.class, args);
    }
}