export type EstadoGeneral = 'ACTIVO' | 'INACTIVO';

export interface SucursalResponse {
  id: number;
  codigo: string;
  nombre: string;
  direccion: string;
  telefono?: string;
  supervisorId?: number;
  supervisorNombre?: string;
  estado: EstadoGeneral;
}

export interface SucursalRequest {
  codigo: string;
  nombre: string;
  direccion: string;
  telefono?: string;
  supervisorId?: number;
}
