package com.quickbite.quejas.service.impl;

import com.quickbite.quejas.dto.reporte.AgenteDesempenoResponse;
import com.quickbite.quejas.dto.reporte.ReporteQuejasResponse;
import com.quickbite.quejas.exception.BusinessException;
import com.quickbite.quejas.model.EstadoQueja;
import com.quickbite.quejas.service.ReporteService;
import com.quickbite.quejas.model.Queja;
import com.quickbite.quejas.model.Usuario;
import com.quickbite.quejas.repository.QuejaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final QuejaRepository quejaRepository;

    @Override
    public ReporteQuejasResponse generar(LocalDate desde, LocalDate hasta, Long sucursalId, Long categoriaId) {
        // FA02 CU15 - rango de fechas invalido
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new BusinessException("El rango de fechas seleccionado para el reporte no es valido.");
        }

        List<Queja> quejas = quejaRepository.findAll().stream()
                .filter(q -> desde == null || !q.getFechaRegistro().toLocalDate().isBefore(desde))
                .filter(q -> hasta == null || !q.getFechaRegistro().toLocalDate().isAfter(hasta))
                .filter(q -> sucursalId == null || q.getSucursal().getId().equals(sucursalId))
                .filter(q -> categoriaId == null || q.getCategoria().getId().equals(categoriaId))
                .toList();

        // FA01 CU15 - sin resultados
        if (quejas.isEmpty()) {
            throw new BusinessException("No existen registros.");
        }

        Map<String, Long> porCategoria = quejas.stream()
                .collect(Collectors.groupingBy(q -> q.getCategoria().getNombre(), Collectors.counting()));
        Map<String, Long> porSucursal = quejas.stream()
                .collect(Collectors.groupingBy(q -> q.getSucursal().getNombre(), Collectors.counting()));
        Map<String, Long> porEstado = quejas.stream()
                .collect(Collectors.groupingBy(q -> q.getEstado().name(), Collectors.counting()));

        double tiempoPromedioHoras = quejas.stream()
                .filter(q -> q.getFechaResolucion() != null)
                .mapToLong(q -> Duration.between(q.getFechaRegistro(), q.getFechaResolucion()).toMinutes())
                .average().orElse(0) / 60.0;

        double satisfaccionPromedio = quejas.stream()
                .flatMap(q -> q.getCalificaciones().stream())
                .mapToInt(c -> c.getCalificacion())
                .average().orElse(0);

        List<AgenteDesempenoResponse> desempeno = quejas.stream()
                .filter(q -> q.getAgenteAsignado() != null)
                .collect(Collectors.groupingBy(Queja::getAgenteAsignado))
                .entrySet().stream()
                .map(e -> {
                    Usuario agente = e.getKey();
                    List<Queja> deAgente = e.getValue();
                    double promedio = deAgente.stream()
                            .flatMap(q -> q.getCalificaciones().stream())
                            .mapToInt(c -> c.getCalificacion())
                            .average().orElse(0);
                    return new AgenteDesempenoResponse(agente.getNombreCompleto(), deAgente.size(), promedio);
                })
                .sorted(Comparator.comparing(AgenteDesempenoResponse::agenteNombre))
                .toList();

        return new ReporteQuejasResponse(quejas.size(), porCategoria, porSucursal, porEstado,
                tiempoPromedioHoras, satisfaccionPromedio, desempeno);
    }
}
