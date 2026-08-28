package com.quickbite.quejas.service;

import com.quickbite.quejas.model.Queja;

/** CU09 - Asignar Queja (asignacion automatica y aleatoria - RN09). */
public interface AsignacionService {

    /** Flujo normal CU09: selecciona aleatoriamente un agente activo y asigna la queja. */
    void asignarAutomaticamente(Queja queja);

    /** CU09 FA03: reasignacion manual por el Supervisor. */
    void reasignarManualmente(Queja queja, Long nuevoAgenteId, Long supervisorId);
}
