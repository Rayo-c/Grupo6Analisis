package com.quickbite.quejas.repository;

import com.quickbite.quejas.model.CategoriaQueja;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaQuejaRepository extends JpaRepository<CategoriaQueja, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}
