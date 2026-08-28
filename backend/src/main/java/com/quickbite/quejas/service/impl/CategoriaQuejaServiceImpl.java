package com.quickbite.quejas.service.impl;

import com.quickbite.quejas.dto.categoria.CategoriaRequest;
import com.quickbite.quejas.dto.categoria.CategoriaResponse;
import com.quickbite.quejas.exception.BusinessException;
import com.quickbite.quejas.exception.ResourceNotFoundException;
import com.quickbite.quejas.model.CategoriaQueja;
import com.quickbite.quejas.model.EstadoGeneral;
import com.quickbite.quejas.repository.CategoriaQuejaRepository;
import com.quickbite.quejas.repository.QuejaRepository;
import com.quickbite.quejas.service.CategoriaQuejaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaQuejaServiceImpl implements CategoriaQuejaService {

    private final CategoriaQuejaRepository categoriaRepository;
    private final QuejaRepository quejaRepository;

    @Override
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        if (categoriaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new BusinessException("Ya existe una categoria con el mismo nombre.");
        }
        CategoriaQueja categoria = CategoriaQueja.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .slaHoras(request.slaHoras())
                .estado(EstadoGeneral.ACTIVO)
                .build();
        categoriaRepository.save(categoria);
        return toResponse(categoria);
    }

    @Override
    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        CategoriaQueja categoria = obtener(id);
        if (!categoria.getNombre().equalsIgnoreCase(request.nombre())
                && categoriaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new BusinessException("Ya existe una categoria con el mismo nombre.");
        }
        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());
        categoria.setSlaHoras(request.slaHoras());
        categoriaRepository.save(categoria);
        return toResponse(categoria);
    }

    @Override
    @Transactional
    public CategoriaResponse cambiarEstado(Long id) {
        CategoriaQueja categoria = obtener(id);

        // FA04 CU06 - no inactivar si tiene quejas asociadas
        if (categoria.getEstado() == EstadoGeneral.ACTIVO) {
            boolean tieneQuejas = quejaRepository.findAll().stream()
                    .anyMatch(q -> q.getCategoria().getId().equals(id));
            if (tieneQuejas) {
                throw new BusinessException("No se puede inactivar la categoria porque tiene quejas asociadas.");
            }
        }

        categoria.setEstado(categoria.getEstado() == EstadoGeneral.ACTIVO ? EstadoGeneral.INACTIVO : EstadoGeneral.ACTIVO);
        categoriaRepository.save(categoria);
        return toResponse(categoria);
    }

    private CategoriaQueja obtener(Long id) {
        return categoriaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existen registros."));
    }

    private CategoriaResponse toResponse(CategoriaQueja c) {
        return new CategoriaResponse(c.getId(), c.getNombre(), c.getDescripcion(), c.getSlaHoras(), c.getEstado());
    }
}
