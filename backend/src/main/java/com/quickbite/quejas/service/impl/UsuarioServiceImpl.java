package com.quickbite.quejas.service.impl;

import com.quickbite.quejas.dto.usuario.CambiarEstadoRequest;
import com.quickbite.quejas.dto.usuario.UsuarioPersonalRequest;
import com.quickbite.quejas.dto.usuario.UsuarioResponse;
import com.quickbite.quejas.exception.BusinessException;
import com.quickbite.quejas.exception.ResourceNotFoundException;
import com.quickbite.quejas.model.*;
import com.quickbite.quejas.repository.QuejaRepository;
import com.quickbite.quejas.repository.SucursalRepository;
import com.quickbite.quejas.repository.UsuarioRepository;
import com.quickbite.quejas.service.NotificacionService;
import com.quickbite.quejas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;
    private final QuejaRepository quejaRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificacionService notificacionService;

    private static final List<EstadoQueja> ESTADOS_ACTIVOS =
            List.of(EstadoQueja.ASIGNADA, EstadoQueja.EN_PROCESO, EstadoQueja.ESCALADA, EstadoQueja.REABIERTA);

    @Override
    public List<UsuarioResponse> listarPersonal(String texto, String rol, String estado, Long sucursalId) {
        return usuarioRepository.findAll().stream()
                .filter(Usuario::esPersonalInterno)
                .filter(u -> texto == null || texto.isBlank()
                        || u.getNombreCompleto().toLowerCase().contains(texto.toLowerCase())
                        || u.getCorreo().toLowerCase().contains(texto.toLowerCase()))
                .filter(u -> rol == null || rol.isBlank() || u.getRol().name().equalsIgnoreCase(rol))
                .filter(u -> estado == null || estado.isBlank() || u.getEstado().name().equalsIgnoreCase(estado))
                .filter(u -> sucursalId == null || (u.getSucursal() != null && u.getSucursal().getId().equals(sucursalId)))
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UsuarioResponse crearPersonal(UsuarioPersonalRequest request) {
        validarRolPersonal(request.rol());

        if (usuarioRepository.existsByCorreoIgnoreCase(request.correo())) {
            throw new BusinessException("El correo electronico ya se encuentra registrado.");
        }

        Sucursal sucursal = resolverSucursal(request.sucursalId());
        String passwordTemporal = UUID.randomUUID().toString().substring(0, 10);

        Usuario personal = Usuario.builder()
                .nombreCompleto(request.nombreCompleto())
                .correo(request.correo())
                .rol(request.rol())
                .estado(EstadoCuenta.ACTIVO)
                .sucursal(sucursal)
                .passwordHash(passwordEncoder.encode(passwordTemporal))
                .build();
        usuarioRepository.save(personal);

        notificacionService.enviar(personal.getCorreo(), TipoNotificacion.CREDENCIALES_PERSONAL,
                "Se creo tu cuenta en el sistema QuickBite. Contrasena temporal: " + passwordTemporal
                        + " (debes cambiarla en tu primer inicio de sesion).", null);

        return toResponse(personal);
    }

    @Override
    @Transactional
    public UsuarioResponse actualizarPersonal(Long id, UsuarioPersonalRequest request) {
        Usuario personal = obtenerPersonal(id);
        validarRolPersonal(request.rol());

        if (!personal.getCorreo().equalsIgnoreCase(request.correo())
                && usuarioRepository.existsByCorreoIgnoreCase(request.correo())) {
            throw new BusinessException("El correo electronico ya se encuentra registrado.");
        }

        personal.setNombreCompleto(request.nombreCompleto());
        personal.setCorreo(request.correo());
        personal.setRol(request.rol());
        personal.setSucursal(resolverSucursal(request.sucursalId()));
        usuarioRepository.save(personal);
        return toResponse(personal);
    }

    @Override
    @Transactional
    public UsuarioResponse cambiarEstadoPersonal(Long id) {
        Usuario personal = obtenerPersonal(id);

        // FA05 CU04 - no inactivar si tiene quejas activas asignadas
        if (personal.getRol() == Rol.ROLE_AGENTE && personal.getEstado() == EstadoCuenta.ACTIVO) {
            long activas = quejaRepository.countByAgenteAsignadoAndEstadoIn(personal, ESTADOS_ACTIVOS);
            if (activas > 0) {
                throw new BusinessException("El usuario no cuenta con permisos para realizar esta accion, o la cuenta se encuentra inactiva.");
            }
        }

        personal.setEstado(personal.getEstado() == EstadoCuenta.ACTIVO ? EstadoCuenta.INACTIVO : EstadoCuenta.ACTIVO);
        usuarioRepository.save(personal);
        return toResponse(personal);
    }

    @Override
    public List<UsuarioResponse> listarClientes(String texto, String estado) {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() == Rol.ROLE_CLIENTE)
                .filter(u -> texto == null || texto.isBlank()
                        || u.getNombreCompleto().toLowerCase().contains(texto.toLowerCase())
                        || u.getCorreo().toLowerCase().contains(texto.toLowerCase()))
                .filter(u -> estado == null || estado.isBlank() || u.getEstado().name().equalsIgnoreCase(estado))
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UsuarioResponse cambiarEstadoCliente(Long id, CambiarEstadoRequest request) {
        Usuario cliente = usuarioRepository.findById(id)
                .filter(u -> u.getRol() == Rol.ROLE_CLIENTE)
                .orElseThrow(() -> new ResourceNotFoundException("No existen registros."));

        // FA06 CU04 - motivo obligatorio para suspender
        if (request.motivo() == null || request.motivo().isBlank()) {
            throw new BusinessException("El campo es obligatorio.");
        }

        cliente.setEstado(cliente.getEstado() == EstadoCuenta.ACTIVO ? EstadoCuenta.SUSPENDIDO : EstadoCuenta.ACTIVO);
        cliente.setMotivoSuspension(request.motivo());
        usuarioRepository.save(cliente);
        return toResponse(cliente);
    }

    // -------- privados --------

    private void validarRolPersonal(Rol rol) {
        // El administrador no puede crear/editar cuentas de Cliente (ver CU02 - autorregistro).
        if (rol == Rol.ROLE_CLIENTE) {
            throw new BusinessException("El administrador no puede crear ni modificar cuentas de Cliente; estas se gestionan mediante autorregistro.");
        }
    }

    private Sucursal resolverSucursal(Long sucursalId) {
        if (sucursalId == null) return null;
        return sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("No existen registros."));
    }

    private Usuario obtenerPersonal(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existen registros."));
        if (!usuario.esPersonalInterno()) {
            throw new ResourceNotFoundException("No existen registros.");
        }
        return usuario;
    }

    private UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(
                u.getId(), u.getNombreCompleto(), u.getCorreo(), u.getTelefono(), u.getRol(), u.getEstado(),
                u.getSucursal() != null ? u.getSucursal().getId() : null,
                u.getSucursal() != null ? u.getSucursal().getNombre() : null,
                u.getFechaCreacion()
        );
    }
}
