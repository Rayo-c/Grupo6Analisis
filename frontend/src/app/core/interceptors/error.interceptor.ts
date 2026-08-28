import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { catchError, throwError } from 'rxjs';

/**
 * Manejo centralizado de errores de la API.
 * Los mensajes del backend (catalogo AN02) ya vienen listos para mostrar al usuario.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        authService.logout();
        router.navigate(['/login']);
      }
      const mensaje = error.error?.mensaje || 'Ocurrio un error inesperado. Intente nuevamente.';
      return throwError(() => new Error(mensaje));
    })
  );
};
