import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ReporteQuejasResponse, FiltrosReporte } from '../models/reporte.model';

/** CU15 - Generar Reportes de Quejas. */
@Injectable({ providedIn: 'root' })
export class ReporteService {
  private readonly baseUrl = `${environment.apiUrl}/reportes`;

  constructor(private http: HttpClient) {}

  generar(filtros: FiltrosReporte): Observable<ReporteQuejasResponse> {
    let params = new HttpParams();
    Object.entries(filtros).forEach(([clave, valor]) => {
      if (valor !== undefined && valor !== null && valor !== '') {
        params = params.set(clave, String(valor));
      }
    });
    return this.http.get<ReporteQuejasResponse>(this.baseUrl, { params });
  }
}
