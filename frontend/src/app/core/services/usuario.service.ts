import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UsuarioResponse, UsuarioPersonalRequest, CambiarEstadoRequest } from '../models/usuario.model';

/**
 * CU04 - Administrar Cuentas de Usuario.
 * Alcance: solo personal interno se crea/edita aqui; clientes solo se consultan/suspenden.
 */
@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly baseUrl = `${environment.apiUrl}/usuarios`;

  constructor(private http: HttpClient) {}

  listarPersonal(texto?: string, rol?: string, estado?: string, sucursalId?: number): Observable<UsuarioResponse[]> {
    let params = new HttpParams();
    if (texto) params = params.set('texto', texto);
    if (rol) params = params.set('rol', rol);
    if (estado) params = params.set('estado', estado);
    if (sucursalId) params = params.set('sucursalId', sucursalId);
    return this.http.get<UsuarioResponse[]>(`${this.baseUrl}/personal`, { params });
  }

  crearPersonal(request: UsuarioPersonalRequest): Observable<UsuarioResponse> {
    return this.http.post<UsuarioResponse>(`${this.baseUrl}/personal`, request);
  }

  actualizarPersonal(id: number, request: UsuarioPersonalRequest): Observable<UsuarioResponse> {
    return this.http.put<UsuarioResponse>(`${this.baseUrl}/personal/${id}`, request);
  }

  cambiarEstadoPersonal(id: number): Observable<UsuarioResponse> {
    return this.http.patch<UsuarioResponse>(`${this.baseUrl}/personal/${id}/estado`, {});
  }

  listarClientes(texto?: string, estado?: string): Observable<UsuarioResponse[]> {
    let params = new HttpParams();
    if (texto) params = params.set('texto', texto);
    if (estado) params = params.set('estado', estado);
    return this.http.get<UsuarioResponse[]>(`${this.baseUrl}/clientes`, { params });
  }

  cambiarEstadoCliente(id: number, request: CambiarEstadoRequest): Observable<UsuarioResponse> {
    return this.http.patch<UsuarioResponse>(`${this.baseUrl}/clientes/${id}/estado`, request);
  }
}
