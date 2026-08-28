package com.quickbite.quejas.service;

import com.quickbite.quejas.dto.usuario.CambiarEstadoRequest;
import com.quickbite.quejas.dto.usuario.UsuarioPersonalRequest;
import com.quickbite.quejas.dto.usuario.UsuarioResponse;

import java.util.List;

/**
 * CU04 - Administrar Cuentas de Usuario.
 * Alcance: unicamente personal interno (Agente, Supervisor, Administrador).
 * Las cuentas de Cliente se crean por autorregistro (CU02); aqui solo se consultan
 * y se les puede cambiar el estado por moderacion.
 */
public interface UsuarioService {
    List<UsuarioResponse> listarPersonal(String texto, String rol, String estado, Long sucursalId);
    UsuarioResponse crearPersonal(UsuarioPersonalRequest request);
    UsuarioResponse actualizarPersonal(Long id, UsuarioPersonalRequest request);
    UsuarioResponse cambiarEstadoPersonal(Long id);

    List<UsuarioResponse> listarClientes(String texto, String estado);
    UsuarioResponse cambiarEstadoCliente(Long id, CambiarEstadoRequest request);
}
