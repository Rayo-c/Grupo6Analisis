import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  QuejaRequest, QuejaResponse, SeguimientoRequest, ResolverRequest,
  EscalarRequest, ReabrirRequest, ReasignarRequest, CalificacionRequest, FiltrosQueja
} from '../models/queja.model';

/**
 * CU07 Registrar Queja, CU08 Administrar Quejas, CU09 (reasignacion FA03),
 * CU10 Dar Seguimiento, CU11 Resolver, CU12 Escalar, CU13 Reabrir, CU14 Calificar.
 */
@Injectable({ providedIn: 'root' })
export class QuejaService {
  private readonly baseUrl = `${environment.apiUrl}/quejas`;

  constructor(private http: HttpClient) {}

  // CU07 - cliente autenticado
  registrar(request: QuejaRequest): Observable<QuejaResponse> {
    return this.http.post<QuejaResponse>(this.baseUrl, request);
  }

  // CU07 - FA01, invitado sin cuenta
  registrarComoInvitado(request: QuejaRequest): Observable<QuejaResponse> {
    return this.http.post<QuejaResponse>(`${this.baseUrl}/invitado`, request);
  }

  // CU07 - consulta publica por numero de seguimiento (invitados)
  consultarPorNumero(numero: string): Observable<QuejaResponse> {
    return this.http.get<QuejaResponse>(`${this.baseUrl}/seguimiento/${numero}`);
  }

  // CU08 - listar / filtrar (agente ve solo lo asignado, supervisor ve su sucursal - resuelto en backend)
  listar(filtros: FiltrosQueja): Observable<QuejaResponse[]> {
    let params = new HttpParams();
    Object.entries(filtros).forEach(([clave, valor]) => {
      if (valor !== undefined && valor !== null && valor !== '') {
        params = params.set(clave, String(valor));
      }
    });
    return this.http.get<QuejaResponse[]>(this.baseUrl, { params });
  }

  // CU08 - detalle
  detalle(id: number): Observable<QuejaResponse> {
    return this.http.get<QuejaResponse>(`${this.baseUrl}/${id}`);
  }

  // CU10 - Dar Seguimiento
  agregarSeguimiento(id: number, request: SeguimientoRequest): Observable<QuejaResponse> {
    return this.http.post<QuejaResponse>(`${this.baseUrl}/${id}/seguimiento`, request);
  }

  // CU11 - Resolver
  resolver(id: number, request: ResolverRequest): Observable<QuejaResponse> {
    return this.http.post<QuejaResponse>(`${this.baseUrl}/${id}/resolver`, request);
  }

  // CU12 - Escalar
  escalar(id: number, request: EscalarRequest): Observable<QuejaResponse> {
    return this.http.post<QuejaResponse>(`${this.baseUrl}/${id}/escalar`, request);
  }

  // CU13 - Reabrir (dentro de RN05: 5 dias tras la resolucion)
  reabrir(id: number, request: ReabrirRequest): Observable<QuejaResponse> {
    return this.http.post<QuejaResponse>(`${this.baseUrl}/${id}/reabrir`, request);
  }

  // CU14 - Calificar (RN08: una calificacion por resolucion)
  calificar(id: number, request: CalificacionRequest): Observable<QuejaResponse> {
    return this.http.post<QuejaResponse>(`${this.baseUrl}/${id}/calificacion`, request);
  }

  // CU09 - FA03, reasignacion manual (Supervisor)
  reasignar(id: number, request: ReasignarRequest): Observable<QuejaResponse> {
    return this.http.post<QuejaResponse>(`${this.baseUrl}/${id}/reasignar`, request);
  }
}
