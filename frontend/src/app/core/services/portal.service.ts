import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface InfoPortal {
  mision: string;
  vision: string;
  valores: string;
}

/** CU00 - Portal (Pagina de Inicio). */
@Injectable({ providedIn: 'root' })
export class PortalService {
  private readonly baseUrl = `${environment.apiUrl}/portal`;

  constructor(private http: HttpClient) {}

  obtenerInfo(): Observable<InfoPortal> {
    return this.http.get<InfoPortal>(`${this.baseUrl}/info`);
  }

  actualizarInfo(info: InfoPortal): Observable<InfoPortal> {
    return this.http.put<InfoPortal>(`${this.baseUrl}/info`, info);
  }

  restaurarInfo(): Observable<InfoPortal> {
    return this.http.post<InfoPortal>(`${this.baseUrl}/info/restaurar`, {});
  }
}