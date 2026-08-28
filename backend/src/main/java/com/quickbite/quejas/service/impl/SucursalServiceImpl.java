package com.quickbite.quejas.service.impl;

import com.quickbite.quejas.dto.sucursal.SucursalRequest;
import com.quickbite.quejas.dto.sucursal.SucursalResponse;
import com.quickbite.quejas.exception.BusinessException;
import com.quickbite.quejas.exception.ResourceNotFoundException;
import com.quickbite.quejas.model.EstadoGeneral;
import com.quickbite.quejas.model.EstadoQueja;
import com.quickbite.quejas.model.Rol;
import com.quickbite.quejas.model.Sucursal;
import com.quickbite.quejas.model.Usuario;
import com.quickbite.quejas.repository.QuejaRepository;
import com.quickbite.quejas.repository.SucursalRepository;
import com.quickbite.quejas.repository.UsuarioRepository;
import com.quickbite.quejas.service.SucursalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SucursalServiceImpl implements SucursalService {

    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final QuejaRepository quejaRepository;

    @Override
    public List<SucursalResponse> listar() {
        return sucursalRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public SucursalResponse crear(SucursalRequest request) {
        // FA03 CU05 - codigo de sucursal duplicado
        if (sucursalRepository.existsByCodigoIgnoreCase(request.codigo())) {
            throw new BusinessException("El codigo de sucursal ya existe.");
        }
        Sucursal sucursal = Sucursal.builder()
                .codigo(request.codigo())
                .nombre(request.nombre())
                .direccion(request.direccion())
                .telefono(request.telefono())
                .supervisor(resolverSupervisor(request.supervisorId()))
                .estado(EstadoGeneral.ACTIVO)
                .build();
        sucursalRepository.save(sucursal);
        return toResponse(sucursal);
    }

    @Override
    @Transactional
    public SucursalResponse actualizar(Long id, SucursalRequest request) {
        Sucursal sucursal = obtener(id);
        if (!sucursal.getCodigo().equalsIgnoreCase(request.codigo())
                && sucursalRepository.existsByCodigoIgnoreCase(request.codigo())) {
            throw new BusinessException("El codigo de sucursal ya existe.");
        }
        sucursal.setCodigo(request.codigo());
        sucursal.setNombre(request.nombre());
        sucursal.setDireccion(request.direccion());
        sucursal.setTelefono(request.telefono());
        sucursal.setSupervisor(resolverSupervisor(request.supervisorId()));
        sucursalRepository.save(sucursal);
        return toResponse(sucursal);
    }

    @Override
    @Transactional
    public SucursalResponse cambiarEstado(Long id) {
        Sucursal sucursal = obtener(id);

        // FA04 CU05 - no inactivar si tiene quejas activas sin resolver
        if (sucursal.getEstado() == EstadoGeneral.ACTIVO) {
            boolean tieneActivas = quejaRepository.findAll().stream()
                    .anyMatch(q -> q.getSucursal().getId().equals(id)
                            && q.getEstado() != EstadoQueja.RESUELTA && q.getEstado() != EstadoQueja.CERRADA);
            if (tieneActivas) {
                throw new BusinessException("No se puede inactivar la sucursal: existen quejas activas sin resolver asociadas.");
            }
        }

        sucursal.setEstado(sucursal.getEstado() == EstadoGeneral.ACTIVO ? EstadoGeneral.INACTIVO : EstadoGeneral.ACTIVO);
        sucursalRepository.save(sucursal);
        return toResponse(sucursal);
    }

    private Usuario resolverSupervisor(Long supervisorId) {
        if (supervisorId == null) return null;
        Usuario supervisor = usuarioRepository.findById(supervisorId)
                .orElseThrow(() -> new ResourceNotFoundException("No existen registros."));
        if (supervisor.getRol() != Rol.ROLE_SUPERVISOR) {
            throw new BusinessException("El usuario seleccionado no tiene el rol de Supervisor.");
        }
        return supervisor;
    }

    private Sucursal obtener(Long id) {
        return sucursalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existen registros."));
    }

    private SucursalResponse toResponse(Sucursal s) {
        return new SucursalResponse(s.getId(), s.getCodigo(), s.getNombre(), s.getDireccion(), s.getTelefono(),
                s.getSupervisor() != null ? s.getSupervisor().getId() : null,
                s.getSupervisor() != null ? s.getSupervisor().getNombreCompleto() : null,
                s.getEstado());
    }
}
