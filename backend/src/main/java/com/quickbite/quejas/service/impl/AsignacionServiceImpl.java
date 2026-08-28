package com.quickbite.quejas.service.impl;

import com.quickbite.quejas.exception.BusinessException;
import com.quickbite.quejas.exception.ResourceNotFoundException;
import com.quickbite.quejas.model.*;
import com.quickbite.quejas.repository.QuejaRepository;
import com.quickbite.quejas.repository.UsuarioRepository;
import com.quickbite.quejas.service.AsignacionService;
import com.quickbite.quejas.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * CU09 - Asignar Queja.
 * RN09: la asignacion inicial es automatica y aleatoria entre los agentes activos
 * de la sucursal; no requiere intervencion del Supervisor. Si nadie esta disponible,
 * o todos superan el limite de carga, se aplican las reglas de las FA01/FA02.
 */
@Service
@RequiredArgsConstructor
public class AsignacionServiceImpl implements AsignacionService {

    private final UsuarioRepository usuarioRepository;
    private final QuejaRepository quejaRepository;
    private final NotificacionService notificacionService;
    private final SecureRandom random = new SecureRandom();

    @Value("${app.negocio.max-quejas-activas-por-agente}")
    private int maxQuejasActivasPorAgente;

    private static final List<EstadoQueja> ESTADOS_ACTIVOS =
            List.of(EstadoQueja.ASIGNADA, EstadoQueja.EN_PROCESO, EstadoQueja.ESCALADA, EstadoQueja.REABIERTA);

    @Override
    public void asignarAutomaticamente(Queja queja) {
        List<Usuario> agentesActivos = usuarioRepository
                .findByRolAndEstadoAndSucursal(Rol.ROLE_AGENTE, EstadoCuenta.ACTIVO, queja.getSucursal());

        if (agentesActivos.isEmpty()) {
            // FA01 - no hay agentes activos disponibles: la queja permanece "Registrada".
            notificarSupervisor(queja, "No hay agentes disponibles para asignar la queja "
                    + queja.getNumeroSeguimiento() + ". Se requiere gestion manual.");
            return;
        }

        List<Usuario> conCupo = agentesActivos.stream()
                .filter(a -> quejaRepository.countByAgenteAsignadoAndEstadoIn(a, ESTADOS_ACTIVOS) < maxQuejasActivasPorAgente)
                .toList();

        Usuario elegido;
        if (!conCupo.isEmpty()) {
            // Seleccion aleatoria (RN09) entre los agentes con cupo disponible.
            elegido = conCupo.get(random.nextInt(conCupo.size()));
        } else {
            // FA02 - todos superan el limite: se asigna al de menor carga y se notifica al supervisor.
            elegido = agentesActivos.stream()
                    .min(Comparator.comparingLong(a -> quejaRepository.countByAgenteAsignadoAndEstadoIn(a, ESTADOS_ACTIVOS)))
                    .orElseThrow();
            notificarSupervisor(queja, "Todos los agentes de la sucursal superan la carga maxima recomendada. "
                    + "La queja " + queja.getNumeroSeguimiento() + " se asigno de todas formas al agente con menor carga.");
        }

        aplicarAsignacion(queja, elegido);
    }

    @Override
    public void reasignarManualmente(Queja queja, Long nuevoAgenteId, Long supervisorId) {
        Usuario nuevoAgente = usuarioRepository.findById(nuevoAgenteId)
                .orElseThrow(() -> new ResourceNotFoundException("No existen registros."));

        if (nuevoAgente.getRol() != Rol.ROLE_AGENTE || nuevoAgente.getEstado() != EstadoCuenta.ACTIVO) {
            throw new BusinessException("El usuario no cuenta con permisos para realizar esta accion, o la cuenta se encuentra inactiva.");
        }

        Usuario agenteAnterior = queja.getAgenteAsignado();
        aplicarAsignacion(queja, nuevoAgente);

        if (agenteAnterior != null) {
            notificacionService.enviar(agenteAnterior.getCorreo(), TipoNotificacion.ASIGNACION_QUEJA,
                    "La queja " + queja.getNumeroSeguimiento() + " fue reasignada a otro agente.", queja.getId());
        }
    }

    private void aplicarAsignacion(Queja queja, Usuario agente) {
        queja.setAgenteAsignado(agente);
        if (queja.getEstado() == EstadoQueja.REGISTRADA) {
            queja.setEstado(EstadoQueja.ASIGNADA);
        }
        queja.setFechaAsignacion(LocalDateTime.now());
        quejaRepository.save(queja);

        notificacionService.enviar(agente.getCorreo(), TipoNotificacion.ASIGNACION_QUEJA,
                "Se te ha asignado la queja " + queja.getNumeroSeguimiento() + ".", queja.getId());
    }

    private void notificarSupervisor(Queja queja, String detalle) {
        Usuario supervisor = queja.getSucursal().getSupervisor();
        if (supervisor != null) {
            notificacionService.enviar(supervisor.getCorreo(), TipoNotificacion.ALERTA_ESCALAMIENTO, detalle, queja.getId());
        }
    }
}
