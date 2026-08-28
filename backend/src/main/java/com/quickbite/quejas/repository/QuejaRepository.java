package com.quickbite.quejas.repository;

import com.quickbite.quejas.model.EstadoQueja;
import com.quickbite.quejas.model.Queja;
import com.quickbite.quejas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QuejaRepository extends JpaRepository<Queja, Long>, JpaSpecificationExecutor<Queja> {

    Optional<Queja> findByNumeroSeguimiento(String numeroSeguimiento);

    long countByAgenteAsignadoAndEstadoIn(Usuario agente, List<EstadoQueja> estados);

    List<Queja> findByAgenteAsignado(Usuario agente);

    /** CU11 - postcondicion: cierre automatico tras N dias sin reapertura. */
    @Query("SELECT q FROM Queja q WHERE q.estado = 'RESUELTA' AND q.fechaResolucion <= :limite")
    List<Queja> findResueltasParaCierreAutomatico(@Param("limite") LocalDateTime limite);

    /** CU12 - escalamiento automatico por vencimiento de SLA (RN05). */
    @Query("SELECT q FROM Queja q WHERE q.estado IN ('ASIGNADA','EN_PROCESO')")
    List<Queja> findActivasParaRevisionSla();
}
