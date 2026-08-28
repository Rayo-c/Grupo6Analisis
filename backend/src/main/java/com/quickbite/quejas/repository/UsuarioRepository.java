package com.quickbite.quejas.repository;

import com.quickbite.quejas.model.EstadoCuenta;
import com.quickbite.quejas.model.Rol;
import com.quickbite.quejas.model.Sucursal;
import com.quickbite.quejas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCase(String correo);

    Optional<Usuario> findByTokenVerificacion(String token);

    Optional<Usuario> findByTokenRecuperacion(String token);

    /** RN09 - agentes activos disponibles de una sucursal, para la asignacion aleatoria. */
    List<Usuario> findByRolAndEstadoAndSucursal(Rol rol, EstadoCuenta estado, Sucursal sucursal);
}
