package com.quickbite.quejas.scheduler;

import com.quickbite.quejas.service.QuejaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tareas automaticas del sistema:
 * - CU11 (postcondicion): cierre automatico de quejas resueltas tras el plazo configurado.
 * - CU12 (requerimiento): escalamiento automatico de quejas que superan el SLA de su categoria (RN05).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class QuejaScheduler {

    private final QuejaService quejaService;

    @Scheduled(cron = "0 0 * * * *") // cada hora
    public void cerrarQuejasResueltas() {
        log.info("Ejecutando cierre automatico de quejas resueltas (CU11).");
        quejaService.procesarCierresAutomaticos();
    }

    @Scheduled(cron = "0 */15 * * * *") // cada 15 minutos
    public void escalarQuejasPorSla() {
        log.info("Revisando vencimientos de SLA para escalamiento automatico (CU12 / RN05).");
        quejaService.procesarEscalamientosPorSla();
    }
}
