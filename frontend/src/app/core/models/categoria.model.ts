import { EstadoGeneral } from './sucursal.model';

export interface CategoriaResponse {
  id: number;
  nombre: string;
  descripcion?: string;
  slaHoras: number;
  estado: EstadoGeneral;
}

export interface CategoriaRequest {
  nombre: string;
  descripcion?: string;
  slaHoras: number;
}
