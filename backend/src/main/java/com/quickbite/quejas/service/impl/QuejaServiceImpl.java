package com.quickbite.quejas.service.impl;

import com.quickbite.quejas.dto.queja.*;
import com.quickbite.quejas.exception.BusinessException;
import com.quickbite.quejas.exception.ResourceNotFoundException;
import com.quickbite.quejas.model.*;
import com.quickbite.quejas.repository.*;
import com.quickbite.quejas.service.AsignacionService;
import com.quickbite.quejas.service.NotificacionService;
import com.quickbite.quejas.service.QuejaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * CU07 Registrar Queja, CU08 Administrar Quejas (listar/detalle), CU09 (reasignacion manual FA03),
 * CU10 Dar Seguimiento, CU11 Resolver, CU12 Escalar, CU13 Reabrir, CU14 Calificar Resolucion.
 */
@Service
@RequiredArgsConstructor
public class QuejaServiceImpl implements QuejaService {

    private final QuejaRepository quejaRepository;
    private final SucursalRepository sucursalRepository;
    private final CategoriaQuejaRepository categoriaRepository;
    private final SeguimientoQuejaRepository seguimientoRepository;
    private final CalificacionQuejaRepository calificacionRepository;
    private final AsignacionService asignacionService;
    private final NotificacionService notificacionService;
    private final SecureRandom random = new SecureRandom();

    @Value("${app.negocio.dias-limite-reapertura}")
    private int diasLimiteReapertura;

    @Value("${app.negocio.dias-auto-cierre}")
    private int diasAutoCierre;

    // ---------------------------------------------------------------- CU07
    @Override
    @Transactional
    public QuejaResponse registrar(QuejaRequest request, Usuario clienteAutenticado) {
        Sucursal sucursal = sucursalRepository.findById(request.sucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("No existen registros."));
        CategoriaQueja categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("No existen registros."));

        // FA01 CU07 - registro como invitado si no hay cliente autenticado
        if (clienteAutenticado == null &&
                (request.invitadoNombre() == null || request.invitadoNombre().isBlank()
                        || request.invitadoCorreo() == null || request.invitadoCorreo().isBlank())) {
            throw new BusinessException("El campo es obligatorio.");
        }

        Queja queja = Queja.builder()
                .numeroSeguimiento(generarNumeroSeguimiento())
                .cliente(clienteAutenticado)
                .invitadoNombre(clienteAutenticado == null ? request.invitadoNombre() : null)
                .invitadoCorreo(clienteAutenticado == null ? request.invitadoCorreo() : null)
                .invitadoTelefono(clienteAutenticado == null ? request.invitadoTelefono() : null)
                .sucursal(sucursal)
                .categoria(categoria)
                .fechaHoraIncidente(request.fechaHoraIncidente())
                .descripcion(request.descripcion())
                .evidenciaUrl(request.evidenciaUrl())
                .estado(EstadoQueja.REGISTRADA)
                .build();
        quejaRepository.save(queja);

        // RN09 - asignacion automatica y aleatoria inmediatamente despues del registro
        asignacionService.asignarAutomaticamente(queja);

        notificacionService.enviar(queja.correoContacto(), TipoNotificacion.CONFIRMACION_REGISTRO,
                "Tu queja fue registrada con el numero de seguimiento " + queja.getNumeroSeguimiento() + ".",
                queja.getId());

        return toResponse(queja);
    }

    // ---------------------------------------------------------------- CU08
    @Override
    public List<QuejaResponse> listar(String numeroSeguimiento, Long sucursalId, Long categoriaId,
                                       String estado, LocalDate desde, LocalDate hasta, Usuario actor) {

        Specification<Queja> spec = Specification.where(null);

        if (actor.getRol() == Rol.ROLE_AGENTE) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("agenteAsignado"), actor));
        } else if (actor.getRol() == Rol.ROLE_SUPERVISOR) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("sucursal"), actor.getSucursal()));
        }
        if (numeroSeguimiento != null && !numeroSeguimiento.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("numeroSeguimiento"), numeroSeguimiento));
        }
        if (sucursalId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("sucursal").get("id"), sucursalId));
        }
        if (categoriaId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("categoria").get("id"), categoriaId));
        }
        if (estado != null && !estado.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("estado"), EstadoQueja.valueOf(estado)));
        }
        if (desde != null) {
            spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("fechaRegistro"), desde.atStartOfDay()));
        }
        if (hasta != null) {
            spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("fechaRegistro"), hasta.atTime(23, 59, 59)));
        }

        return quejaRepository.findAll(spec).stream().map(this::toResponse).toList();
    }

    @Override
    public QuejaResponse detalle(Long id, Usuario actor) {
        Queja queja = obtener(id);
        validarAcceso(queja, actor);
        return toResponse(queja);
    }

    // ---------------------------------------------------------------- CU10
    @Override
    @Transactional
    public QuejaResponse agregarSeguimiento(Long id, SeguimientoRequest request, Usuario actor) {
        Queja queja = obtener(id);
        validarAcceso(queja, actor);

        if (request.comentario() == null || request.comentario().isBlank()) {
            throw new BusinessException("Debe ingresar un comentario antes de continuar.");
        }

        SeguimientoQueja seguimiento = SeguimientoQueja.builder()
                .queja(queja)
                .usuario(actor)
                .comentario(request.comentario())
                .evidenciaUrl(request.evidenciaUrl())
                .build();
        seguimientoRepository.save(seguimiento);

        if (queja.getEstado() == EstadoQueja.ASIGNADA || queja.getEstado() == EstadoQueja.REABIERTA) {
            queja.setEstado(EstadoQueja.EN_PROCESO);
            quejaRepository.save(queja);
        }

        notificacionService.enviar(queja.correoContacto(), TipoNotificacion.ACTUALIZACION_ESTADO,
                "Tu queja " + queja.getNumeroSeguimiento() + " tiene una nueva actualizacion.", queja.getId());

        return toResponse(queja);
    }

    // ---------------------------------------------------------------- CU11
    @Override
    @Transactional
    public QuejaResponse resolver(Long id, ResolverRequest request, Usuario actor) {
        Queja queja = obtener(id);
        validarAcceso(queja, actor);

        if (queja.getEstado() != EstadoQueja.ASIGNADA && queja.getEstado() != EstadoQueja.EN_PROCESO
                && queja.getEstado() != EstadoQueja.REABIERTA) {
            throw new BusinessException("La queja no se encuentra en un estado valido para resolver.");
        }
        if (request.solucion() == null || request.solucion().isBlank()) {
            throw new BusinessException("No se puede resolver una queja sin una respuesta al cliente.");
        }

        queja.setSolucion(request.solucion());
        queja.setTipoResolucion(request.tipoResolucion());
        queja.setEstado(EstadoQueja.RESUELTA);
        queja.setFechaResolucion(LocalDateTime.now());
        queja.setNumeroResolucion(queja.getNumeroResolucion() + 1); // RN08
        quejaRepository.save(queja);

        notificacionService.enviar(queja.correoContacto(), TipoNotificacion.RESOLUCION_QUEJA,
                "Tu queja " + queja.getNumeroSeguimiento() + " fue resuelta. Cuentanos tu experiencia calificando la atencion recibida.",
                queja.getId());

        return toResponse(queja);
    }

    // ---------------------------------------------------------------- CU12
    @Override
    @Transactional
    public QuejaResponse escalar(Long id, EscalarRequest request, Usuario actor) {
        Queja queja = obtener(id);
        validarAcceso(queja, actor);

        if (queja.getEstado() != EstadoQueja.ASIGNADA && queja.getEstado() != EstadoQueja.EN_PROCESO) {
            throw new BusinessException("La queja no se encuentra en un estado valido para escalar.");
        }
        if (request.motivo() == null || request.motivo().isBlank()) {
            throw new BusinessException("El campo es obligatorio.");
        }

        queja.setEstado(EstadoQueja.ESCALADA);
        queja.setMotivoEscalamiento(request.motivo());
        queja.setNivelEscalamiento(request.nivel());
        quejaRepository.save(queja);

        Usuario supervisor = queja.getSucursal().getSupervisor();
        if (supervisor != null) {
            notificacionService.enviar(supervisor.getCorreo(), TipoNotificacion.ALERTA_ESCALAMIENTO,
                    "La queja " + queja.getNumeroSeguimiento() + " fue escalada. Motivo: " + request.motivo(), queja.getId());
        }
        notificacionService.enviar(queja.correoContacto(), TipoNotificacion.ALERTA_ESCALAMIENTO,
                "Tu queja " + queja.getNumeroSeguimiento() + " fue escalada a un nivel superior de atencion.", queja.getId());

        return toResponse(queja);
    }

    // ---------------------------------------------------------------- CU13
    @Override
    @Transactional
    public QuejaResponse reabrir(Long id, ReabrirRequest request, Usuario cliente) {
        Queja queja = obtener(id);

        if (queja.getCliente() == null || !queja.getCliente().getId().equals(cliente.getId())) {
            throw new BusinessException("El usuario no cuenta con permisos para realizar esta accion.");
        }
        if (queja.getEstado() != EstadoQueja.RESUELTA) {
            throw new BusinessException("La queja no se encuentra en un estado valido para reabrir.");
        }
        if (queja.getVecesReabierta() >= 1) {
            throw new BusinessException("Una queja unicamente puede reabrirse una vez.");
        }
        if (queja.getFechaResolucion() == null
                || queja.getFechaResolucion().plusDays(diasLimiteReapertura).isBefore(LocalDateTime.now())) {
            // FA01 CU13 - plazo vencido
            throw new BusinessException("Solo se pueden reabrir quejas resueltas dentro de los " + diasLimiteReapertura + " dias posteriores a su resolucion.");
        }
        if (request.motivo() == null || request.motivo().isBlank()) {
            throw new BusinessException("El campo es obligatorio.");
        }

        queja.setEstado(EstadoQueja.REABIERTA);
        queja.setMotivoReapertura(request.motivo());
        queja.setVecesReabierta(queja.getVecesReabierta() + 1);
        // RN08: la calificacion de la resolucion anterior se conserva; numeroResolucion NO se decrementa,
        // por lo que al resolverse de nuevo (numeroResolucion + 1) se habilita una calificacion independiente.
        quejaRepository.save(queja);

        if (queja.getAgenteAsignado() != null) {
            notificacionService.enviar(queja.getAgenteAsignado().getCorreo(), TipoNotificacion.REAPERTURA_QUEJA,
                    "La queja " + queja.getNumeroSeguimiento() + " fue reabierta por el cliente. Motivo: " + request.motivo(),
                    queja.getId());
        }

        return toResponse(queja);
    }

    // ---------------------------------------------------------------- CU14
    @Override
    @Transactional
    public QuejaResponse calificar(Long id, CalificacionRequest request, Usuario cliente) {
        Queja queja = obtener(id);

        if (queja.getCliente() == null || !queja.getCliente().getId().equals(cliente.getId())) {
            throw new BusinessException("El usuario no cuenta con permisos para realizar esta accion.");
        }
        if (queja.getEstado() != EstadoQueja.RESUELTA) {
            throw new BusinessException("La queja no se encuentra en estado Resuelta.");
        }
        // RN08 - una calificacion por resolucion
        if (calificacionRepository.findByQuejaAndNumeroResolucion(queja, queja.getNumeroResolucion()).isPresent()) {
            throw new BusinessException("La queja ya fue calificada previamente.");
        }

        CalificacionQueja calificacion = CalificacionQueja.builder()
                .queja(queja)
                .numeroResolucion(queja.getNumeroResolucion())
                .calificacion(request.calificacion())
                .comentario(request.comentario())
                .build();
        calificacionRepository.save(calificacion);

        // FA04 CU14 - calificacion baja: alerta al supervisor
        if (request.calificacion() <= 2 && queja.getSucursal().getSupervisor() != null) {
            notificacionService.enviar(queja.getSucursal().getSupervisor().getCorreo(),
                    TipoNotificacion.ALERTA_INSATISFACCION,
                    "La queja " + queja.getNumeroSeguimiento() + " recibio una calificacion baja (" + request.calificacion() + "/5).",
                    queja.getId());
        }

        return toResponse(queja);
    }

    // ---------------------------------------------------------------- CU09 FA03
    @Override
    @Transactional
    public QuejaResponse reasignar(Long id, ReasignarRequest request, Usuario supervisor) {
        Queja queja = obtener(id);
        if (!queja.getSucursal().getId().equals(supervisor.getSucursal() != null ? supervisor.getSucursal().getId() : null)) {
            throw new BusinessException("El usuario no cuenta con permisos para realizar esta accion.");
        }
        asignacionService.reasignarManualmente(queja, request.nuevoAgenteId(), supervisor.getId());
        return toResponse(obtener(id));
    }

    // ---------------------------------------------------------------- CU07 (consulta publica por numero)
    @Override
    public QuejaResponse consultarPorNumeroPublico(String numeroSeguimiento) {
        Queja queja = quejaRepository.findByNumeroSeguimiento(numeroSeguimiento)
                .orElseThrow(() -> new ResourceNotFoundException("El numero de seguimiento no existe."));
        return toResponse(queja);
    }

    // ---------------------------------------------------------------- Procesos automaticos
    @Override
    @Transactional
    public void procesarCierresAutomaticos() {
        LocalDateTime limite = LocalDateTime.now().minusDays(diasAutoCierre);
        List<Queja> resueltas = quejaRepository.findResueltasParaCierreAutomatico(limite);
        for (Queja q : resueltas) {
            q.setEstado(EstadoQueja.CERRADA);
            q.setFechaCierre(LocalDateTime.now());
            quejaRepository.save(q);
        }
    }

    @Override
    @Transactional
    public void procesarEscalamientosPorSla() {
        for (Queja q : quejaRepository.findActivasParaRevisionSla()) {
            int slaHoras = q.getCategoria().getSlaHoras();
            LocalDateTime limite = (q.getFechaAsignacion() != null ? q.getFechaAsignacion() : q.getFechaRegistro())
                    .plusHours(slaHoras);
            if (LocalDateTime.now().isAfter(limite) && q.getEstado() != EstadoQueja.ESCALADA) {
                q.setEstado(EstadoQueja.ESCALADA);
                q.setMotivoEscalamiento("Escalamiento automatico por vencimiento del SLA de la categoria (" + slaHoras + "h).");
                q.setNivelEscalamiento(NivelEscalamiento.SUPERVISOR);
                quejaRepository.save(q);

                Usuario supervisor = q.getSucursal().getSupervisor();
                if (supervisor != null) {
                    notificacionService.enviar(supervisor.getCorreo(), TipoNotificacion.ALERTA_ESCALAMIENTO,
                            "La queja " + q.getNumeroSeguimiento() + " se escalo automaticamente por vencimiento de SLA.",
                            q.getId());
                }
            }
        }
    }

    // ---------------------------------------------------------------- privados

    private void validarAcceso(Queja queja, Usuario actor) {
        if (actor.getRol() == Rol.ROLE_AGENTE
                && (queja.getAgenteAsignado() == null || !queja.getAgenteAsignado().getId().equals(actor.getId()))) {
            throw new BusinessException("El usuario no cuenta con permisos para realizar esta accion.");
        }
        if (actor.getRol() == Rol.ROLE_SUPERVISOR
                && !queja.getSucursal().getId().equals(actor.getSucursal() != null ? actor.getSucursal().getId() : null)) {
            throw new BusinessException("El usuario no cuenta con permisos para realizar esta accion.");
        }
    }

    private Queja obtener(Long id) {
        return quejaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("El numero de seguimiento no existe."));
    }

    private String generarNumeroSeguimiento() {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sufijo = String.format("%04d", random.nextInt(10000));
        String numero = "QJ-" + fecha + "-" + sufijo;
        // Garantiza unicidad (muy improbable colision, pero se valida igual).
        while (quejaRepository.findByNumeroSeguimiento(numero).isPresent()) {
            sufijo = String.format("%04d", random.nextInt(10000));
            numero = "QJ-" + fecha + "-" + sufijo;
        }
        return numero;
    }

    private QuejaResponse toResponse(Queja q) {
        List<SeguimientoResponse> historial = seguimientoRepository.findByQuejaOrderByFechaAsc(q).stream()
                .map(s -> new SeguimientoResponse(s.getId(), s.getUsuario().getNombreCompleto(), s.getComentario(), s.getEvidenciaUrl(), s.getFecha()))
                .toList();
        List<CalificacionResponse> calificaciones = q.getCalificaciones().stream()
                .map(c -> new CalificacionResponse(c.getNumeroResolucion(), c.getCalificacion(), c.getComentario(), c.getFecha()))
                .toList();

        return new QuejaResponse(
                q.getId(), q.getNumeroSeguimiento(),
                q.nombreContacto(), q.correoContacto(), q.esInvitado(),
                q.getSucursal().getNombre(), q.getCategoria().getNombre(),
                q.getFechaHoraIncidente(), q.getDescripcion(), q.getEvidenciaUrl(),
                q.getEstado(), q.getAgenteAsignado() != null ? q.getAgenteAsignado().getNombreCompleto() : null,
                q.getNumeroResolucion(), q.getSolucion(), q.getTipoResolucion(),
                q.getMotivoEscalamiento(), q.getNivelEscalamiento(), q.getVecesReabierta(),
                q.getFechaRegistro(), q.getFechaResolucion(), historial, calificaciones
        );
    }
}
