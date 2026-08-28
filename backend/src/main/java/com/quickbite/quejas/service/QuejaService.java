package com.quickbite.quejas.service;

import com.quickbite.quejas.dto.queja.*;
import com.quickbite.quejas.model.Usuario;

import java.time.LocalDate;
import java.util.List;

public interface QuejaService {
    QuejaResponse registrar(QuejaRequest request, Usuario clienteAutenticado);                  // CU07
    List<QuejaResponse> listar(String numeroSeguimiento, Long sucursalId, Long categoriaId,
                                String estado, LocalDate desde, LocalDate hasta, Usuario actor); // CU08
    QuejaResponse detalle(Long id, Usuario actor);                                               // CU08
    QuejaResponse agregarSeguimiento(Long id, SeguimientoRequest request, Usuario actor);        // CU10
    QuejaResponse resolver(Long id, ResolverRequest request, Usuario actor);                     // CU11
    QuejaResponse escalar(Long id, EscalarRequest request, Usuario actor);                       // CU12
    QuejaResponse reabrir(Long id, ReabrirRequest request, Usuario cliente);                     // CU13
    QuejaResponse calificar(Long id, CalificacionRequest request, Usuario cliente);              // CU14
    QuejaResponse reasignar(Long id, ReasignarRequest request, Usuario supervisor);               // CU09 FA03
    QuejaResponse consultarPorNumeroPublico(String numeroSeguimiento);                             // CU07 - seguimiento publico (invitados)

    void procesarCierresAutomaticos();     // CU11 postcondicion (RN02)
    void procesarEscalamientosPorSla();    // CU12 requerimiento (RN05)
}
