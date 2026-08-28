package com.quickbite.quejas.service;

import com.quickbite.quejas.dto.sucursal.SucursalRequest;
import com.quickbite.quejas.dto.sucursal.SucursalResponse;

import java.util.List;

/** CU05 - Administrar Sucursales. */
public interface SucursalService {
    List<SucursalResponse> listar();
    SucursalResponse crear(SucursalRequest request);
    SucursalResponse actualizar(Long id, SucursalRequest request);
    SucursalResponse cambiarEstado(Long id);
}
