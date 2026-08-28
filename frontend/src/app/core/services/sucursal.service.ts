import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SucursalResponse, SucursalRequest } from '../models/sucursal.model';

/** CU05 - Administrar Sucursales. */
@Injectable({ providedIn: 'root' })
export class SucursalService {
  private readonly baseUrl = `${environment.apiUrl}/sucursales`;

  constructor(private http: HttpClient) {}

  listar(): Observable<SucursalResponse[]> {
    return this.http.get<SucursalResponse[]>(this.baseUrl);
  }

  crear(request: SucursalRequest): Observable<SucursalResponse> {
    return this.http.post<SucursalResponse>(this.baseUrl, request);
  }

  actualizar(id: number, request: SucursalRequest): Observable<SucursalResponse> {
    return this.http.put<SucursalResponse>(`${this.baseUrl}/${id}`, request);
  }

  cambiarEstado(id: number): Observable<SucursalResponse> {
    return this.http.patch<SucursalResponse>(`${this.baseUrl}/${id}/estado`, {});
  }
}
