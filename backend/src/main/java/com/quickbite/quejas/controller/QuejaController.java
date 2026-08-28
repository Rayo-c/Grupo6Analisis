package com.quickbite.quejas.controller;

import com.quickbite.quejas.dto.queja.*;
import com.quickbite.quejas.model.Usuario;
import com.quickbite.quejas.security.CustomUserDetails;
import com.quickbite.quejas.service.QuejaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * CU07 Registrar Queja, CU08 Administrar Quejas, CU09 (reasignacion manual FA03),
 * CU10 Dar Seguimiento, CU11 Resolver, CU12 Escalar, CU13 Reabrir, CU14 Calificar.
 */
@RestController
@RequestMapping("/api/quejas")
@RequiredArgsConstructor
public class QuejaController {

    private final QuejaService quejaService;

    // CU07 - cliente autenticado
    @PostMapping
    public QuejaResponse registrar(@Valid @RequestBody QuejaRequest request,
                                    @AuthenticationPrincipal CustomUserDetails principal) {
        Usuario cliente = principal != null ? principal.getUsuario() : null;
        return quejaService.registrar(request, cliente);
    }

    // CU07 - FA01, registro publico como invitado (sin autenticacion, ver SecurityConfig)
    @PostMapping("/invitado")
    public QuejaResponse registrarComoInvitado(@Valid @RequestBody QuejaRequest request) {
        return quejaService.registrar(request, null);
    }

    // CU08 - listar / filtrar
    @GetMapping
    public List<QuejaResponse> listar(
            @RequestParam(required = false) String numeroSeguimiento,
            @RequestParam(required = false) Long sucursalId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return quejaService.listar(numeroSeguimiento, sucursalId, categoriaId, estado, desde, hasta, principal.getUsuario());
    }

    // CU08 - detalle
    @GetMapping("/{id}")
    public QuejaResponse detalle(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal) {
        return quejaService.detalle(id, principal.getUsuario());
    }

    // CU10 - Dar Seguimiento (Agente / Supervisor)
    @PostMapping("/{id}/seguimiento")
    public QuejaResponse agregarSeguimiento(@PathVariable Long id, @Valid @RequestBody SeguimientoRequest request,
                                             @AuthenticationPrincipal CustomUserDetails principal) {
        return quejaService.agregarSeguimiento(id, request, principal.getUsuario());
    }

    // CU11 - Resolver (Agente / Supervisor)
    @PostMapping("/{id}/resolver")
    public QuejaResponse resolver(@PathVariable Long id, @Valid @RequestBody ResolverRequest request,
                                   @AuthenticationPrincipal CustomUserDetails principal) {
        return quejaService.resolver(id, request, principal.getUsuario());
    }

    // CU12 - Escalar (Agente / Supervisor)
    @PostMapping("/{id}/escalar")
    public QuejaResponse escalar(@PathVariable Long id, @Valid @RequestBody EscalarRequest request,
                                  @AuthenticationPrincipal CustomUserDetails principal) {
        return quejaService.escalar(id, request, principal.getUsuario());
    }

    // CU13 - Reabrir (Cliente)
    @PostMapping("/{id}/reabrir")
    public QuejaResponse reabrir(@PathVariable Long id, @Valid @RequestBody ReabrirRequest request,
                                  @AuthenticationPrincipal CustomUserDetails principal) {
        return quejaService.reabrir(id, request, principal.getUsuario());
    }

    // CU14 - Calificar (Cliente)
    @PostMapping("/{id}/calificacion")
    public QuejaResponse calificar(@PathVariable Long id, @Valid @RequestBody CalificacionRequest request,
                                    @AuthenticationPrincipal CustomUserDetails principal) {
        return quejaService.calificar(id, request, principal.getUsuario());
    }

    // CU09 - FA03, reasignacion manual (Supervisor)
    @PostMapping("/{id}/reasignar")
    public QuejaResponse reasignar(@PathVariable Long id, @Valid @RequestBody ReasignarRequest request,
                                    @AuthenticationPrincipal CustomUserDetails principal) {
        return quejaService.reasignar(id, request, principal.getUsuario());
    }

    // CU07 - consulta publica por numero de seguimiento (invitados sin cuenta), ver SecurityConfig
    @GetMapping("/seguimiento/{numero}")
    public QuejaResponse consultarPorNumero(@PathVariable String numero) {
        return quejaService.consultarPorNumeroPublico(numero);
    }
}
