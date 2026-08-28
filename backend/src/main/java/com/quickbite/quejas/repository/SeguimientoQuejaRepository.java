package com.quickbite.quejas.repository;

import com.quickbite.quejas.model.Queja;
import com.quickbite.quejas.model.SeguimientoQueja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeguimientoQuejaRepository extends JpaRepository<SeguimientoQueja, Long> {
    List<SeguimientoQueja> findByQuejaOrderByFechaAsc(Queja queja);
}
