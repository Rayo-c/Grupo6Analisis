package com.quickbite.quejas.repository;

import com.quickbite.quejas.model.CalificacionQueja;
import com.quickbite.quejas.model.Queja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CalificacionQuejaRepository extends JpaRepository<CalificacionQueja, Long> {
    /** RN08 - valida si ya existe calificacion para la resolucion actual. */
    Optional<CalificacionQueja> findByQuejaAndNumeroResolucion(Queja queja, Integer numeroResolucion);
}
