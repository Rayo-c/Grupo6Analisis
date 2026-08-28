import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CategoriaResponse, CategoriaRequest } from '../models/categoria.model';

/** CU06 - Administrar Categorias de Queja. */
@Injectable({ providedIn: 'root' })
export class CategoriaService {
  private readonly baseUrl = `${environment.apiUrl}/categorias`;

  constructor(private http: HttpClient) {}

  listar(): Observable<CategoriaResponse[]> {
    return this.http.get<CategoriaResponse[]>(this.baseUrl);
  }

  crear(request: CategoriaRequest): Observable<CategoriaResponse> {
    return this.http.post<CategoriaResponse>(this.baseUrl, request);
  }

  actualizar(id: number, request: CategoriaRequest): Observable<CategoriaResponse> {
    return this.http.put<CategoriaResponse>(`${this.baseUrl}/${id}`, request);
  }

  cambiarEstado(id: number): Observable<CategoriaResponse> {
    return this.http.patch<CategoriaResponse>(`${this.baseUrl}/${id}/estado`, {});
  }
}
