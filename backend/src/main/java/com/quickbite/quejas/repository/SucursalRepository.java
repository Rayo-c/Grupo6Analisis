package com.quickbite.quejas.repository;

import com.quickbite.quejas.model.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    boolean existsByCodigoIgnoreCase(String codigo);
    Optional<Sucursal> findByCodigoIgnoreCase(String codigo);
}
