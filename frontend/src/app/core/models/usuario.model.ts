// RN01 - Roles del sistema
export type Rol = 'ROLE_CLIENTE' | 'ROLE_AGENTE' | 'ROLE_SUPERVISOR' | 'ROLE_ADMIN';

export type EstadoCuenta = 'PENDIENTE_VERIFICACION' | 'ACTIVO' | 'INACTIVO' | 'SUSPENDIDO';

export interface UsuarioResponse {
  id: number;
  nombreCompleto: string;
  correo: string;
  telefono?: string;
  rol: Rol;
  estado: EstadoCuenta;
  sucursalId?: number;
  sucursalNombre?: string;
  fechaCreacion: string;
}

// CU04 - Crear/editar personal (Agente, Supervisor, Administrador)
export interface UsuarioPersonalRequest {
  nombreCompleto: string;
  correo: string;
  rol: Rol;
  sucursalId?: number;
}

export interface CambiarEstadoRequest {
  motivo?: string;
}
