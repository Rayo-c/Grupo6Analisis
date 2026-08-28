export interface AgenteDesempenoResponse {
  agenteNombre: string;
  quejasAtendidas: number;
  calificacionPromedio: number;
}

export interface ReporteQuejasResponse {
  totalQuejas: number;
  porCategoria: Record<string, number>;
  porSucursal: Record<string, number>;
  porEstado: Record<string, number>;
  tiempoPromedioResolucionHoras: number;
  indiceSatisfaccionPromedio: number;
  desempenoPorAgente: AgenteDesempenoResponse[];
}

export interface FiltrosReporte {
  desde?: string;
  hasta?: string;
  sucursalId?: number;
  categoriaId?: number;
}
