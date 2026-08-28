import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  LoginRequest, LoginResponse, RegistroClienteRequest,
  RecuperarPasswordRequest, ResetPasswordRequest
} from '../models/auth.model';

const TOKEN_KEY = 'quickbite_token';
const USUARIO_KEY = 'quickbite_usuario';

/** CU01 Iniciar Sesion, CU02 Registrar Cliente, CU03 Recuperar Contrasena. */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = `${environment.apiUrl}/auth`;

  private usuarioActual = signal<LoginResponse | null>(this.cargarSesion());
  readonly usuario = computed(() => this.usuarioActual());
  readonly estaAutenticado = computed(() => !!this.usuarioActual());
  readonly rol = computed(() => this.usuarioActual()?.rol ?? null);

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, request).pipe(
      tap((resp) => this.guardarSesion(resp))
    );
  }

  registrarCliente(request: RegistroClienteRequest): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/registro`, request);
  }

  verificarCuenta(token: string): Observable<void> {
    return this.http.get<void>(`${this.baseUrl}/verificar/${token}`);
  }

  recuperarPassword(request: RecuperarPasswordRequest): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/recuperar-password`, request);
  }

  resetPassword(request: ResetPasswordRequest): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/reset-password`, request);
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USUARIO_KEY);
    this.usuarioActual.set(null);
  }

  obtenerToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  tieneRol(...roles: string[]): boolean {
    const rolActual = this.rol();
    return !!rolActual && roles.includes(rolActual);
  }

  private guardarSesion(resp: LoginResponse): void {
    localStorage.setItem(TOKEN_KEY, resp.token);
    localStorage.setItem(USUARIO_KEY, JSON.stringify(resp));
    this.usuarioActual.set(resp);
  }

  private cargarSesion(): LoginResponse | null {
    const raw = localStorage.getItem(USUARIO_KEY);
    return raw ? JSON.parse(raw) as LoginResponse : null;
  }
}
