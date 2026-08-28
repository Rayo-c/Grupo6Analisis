import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/** Uso en rutas: { ..., canActivate: [roleGuard], data: { roles: ['ROLE_ADMIN'] } } */
export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const rolesPermitidos = route.data?.['roles'] as string[] | undefined;

  if (!authService.estaAutenticado()) {
    router.navigate(['/login']);
    return false;
  }
  if (rolesPermitidos && !authService.tieneRol(...rolesPermitidos)) {
    router.navigate(['/']);
    return false;
  }
  return true;
};
