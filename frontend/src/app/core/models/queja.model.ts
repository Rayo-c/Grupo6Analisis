// RN02 - Estados de la Queja
export type EstadoQueja =
  | 'REGISTRADA' | 'ASIGNADA' | 'EN_PROCESO' | 'ESCALADA'
  | 'RESUELTA' | 'CERRADA' | 'REABIERTA';

export type TipoResolucion = 'REEMBOLSO' | 'REPOSICION' | 'DISCULPA' | 'OTRO';
export type NivelEscalamiento = 'SUPERVISOR' | 'GERENCIA_REGIONAL';

export interface SeguimientoResponse {
  id: number;
  usuarioNombre: string;
  comentario: string;
  evidenciaUrl?: string;
  fecha: string;
}

export interface CalificacionResponse {
  numeroResolucion: number;
  calificacion: number;
  comentario?: string;
  fecha: string;
}

export interface QuejaResponse {
  id: number;
  numeroSeguimiento: string;
  clienteNombre: string;
  clienteCorreo: string;
  esInvitado: boolean;
  sucursalNombre: string;
  categoriaNombre: string;
  fechaHoraIncidente: string;
  descripcion: string;
  evidenciaUrl?: string;
  estado: EstadoQueja;
  agenteAsignadoNombre?: string;
  numeroResolucion: number;
  solucion?: string;
  tipoResolucion?: TipoResolucion;
  motivoEscalamiento?: string;
  nivelEscalamiento?: NivelEscalamiento;
  vecesReabierta: number;
  fechaRegistro: string;
  fechaResolucion?: string;
  historial: SeguimientoResponse[];
  calificaciones: CalificacionResponse[];
}

// CU07 - Registrar Queja
export interface QuejaRequest {
  sucursalId: number;
  categoriaId: number;
  fechaHoraIncidente: string;
  descripcion: string;
  evidenciaUrl?: string;
  invitadoNombre?: string;
  invitadoCorreo?: string;
  invitadoTelefono?: string;
}

// CU10 - Dar Seguimiento
export interface SeguimientoRequest {
  comentario: string;
  evidenciaUrl?: string;
}

// CU11 - Resolver
export interface ResolverRequest {
  solucion: string;
  tipoResolucion: TipoResolucion;
}

// CU12 - Escalar
export interface EscalarRequest {
  motivo: string;
  nivel: NivelEscalamiento;
}

// CU13 - Reabrir
export interface ReabrirRequest {
  motivo: string;
}

// CU09 - FA03 Reasignar
export interface ReasignarRequest {
  nuevoAgenteId: number;
}

// CU14 - Calificar (RN08: una calificacion por resolucion)
export interface CalificacionRequest {
  calificacion: number;
  comentario?: string;
}

export interface FiltrosQueja {
  numeroSeguimiento?: string;
  sucursalId?: number;
  categoriaId?: number;
  estado?: string;
  desde?: string;
  hasta?: string;
}
