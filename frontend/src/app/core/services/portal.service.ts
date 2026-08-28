import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

/** CU00 - Portal (Pagina de Inicio). */
@Injectable({ providedIn: 'root' })
export class PortalService {
  private readonly baseUrl = `${environment.apiUrl}/portal`;

  constructor(private http: HttpClient) {}

  obtenerInfo(): Observable<{ mision: string; vision: string; valores: string }> {
    return this.http.get<{ mision: string; vision: string; valores: string }>(`${this.baseUrl}/info`);
  }
}
