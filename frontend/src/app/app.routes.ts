import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  // CU00 - Portal
  {
    path: '',
    loadComponent: () => import('./features/portal/portal-home.component').then(m => m.PortalHomeComponent)
  },

  // CU01 - Iniciar Sesion
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  // CU02 - Registrar Cliente
  {
    path: 'registro',
    loadComponent: () => import('./features/auth/registro/registro.component').then(m => m.RegistroComponent)
  },
  // CU03 - Recuperar Contrasena
  {
    path: 'recuperar-password',
    loadComponent: () => import('./features/auth/recuperar-password/recuperar-password.component').then(m => m.RecuperarPasswordComponent)
  },

  // CU07 - Registrar Queja (publico: cliente autenticado o invitado)
  {
    path: 'registrar-queja',
    loadComponent: () => import('./features/cliente/registrar-queja/registrar-queja.component').then(m => m.RegistrarQuejaComponent)
  },

  // CU08 (vista Cliente) - Mis Quejas
  {
    path: 'mis-quejas',
    canActivate: [roleGuard],
    data: { roles: ['ROLE_CLIENTE'] },
    loadComponent: () => import('./features/cliente/mis-quejas/mis-quejas.component').then(m => m.MisQuejasComponent)
  },

  // CU08 detalle + CU10/CU11/CU12/CU13/CU14 (Cliente, Agente, Supervisor)
  {
    path: 'quejas/:id',
    canActivate: [authGuard],
    loadComponent: () => import('./features/cliente/detalle-queja/detalle-queja.component').then(m => m.DetalleQuejaComponent)
  },

  // CU08 (vista Agente/Supervisor) - Bandeja de Quejas
  {
    path: 'bandeja-quejas',
    canActivate: [roleGuard],
    data: { roles: ['ROLE_AGENTE', 'ROLE_SUPERVISOR'] },
    loadComponent: () => import('./features/agente-supervisor/bandeja-quejas/bandeja-quejas.component').then(m => m.BandejaQuejasComponent)
  },

  // CU15 - Reportes
  {
    path: 'reportes',
    canActivate: [roleGuard],
    data: { roles: ['ROLE_SUPERVISOR', 'ROLE_ADMIN'] },
    loadComponent: () => import('./features/reportes/reportes.component').then(m => m.ReportesComponent)
  },

  // CU04 - Administrar Cuentas de Usuario
  {
    path: 'admin/cuentas-personal',
    canActivate: [roleGuard],
    data: { roles: ['ROLE_ADMIN'] },
    loadComponent: () => import('./features/admin/cuentas-personal/cuentas-personal.component').then(m => m.CuentasPersonalComponent)
  },
  {
    path: 'admin/cuentas-clientes',
    canActivate: [roleGuard],
    data: { roles: ['ROLE_ADMIN'] },
    loadComponent: () => import('./features/admin/cuentas-clientes/cuentas-clientes.component').then(m => m.CuentasClientesComponent)
  },

  // CU05 - Administrar Sucursales
  {
    path: 'admin/sucursales',
    canActivate: [roleGuard],
    data: { roles: ['ROLE_ADMIN'] },
    loadComponent: () => import('./features/admin/sucursales/sucursales.component').then(m => m.SucursalesComponent)
  },

  // CU06 - Administrar Categorias de Queja
  {
    path: 'admin/categorias',
    canActivate: [roleGuard],
    data: { roles: ['ROLE_ADMIN'] },
    loadComponent: () => import('./features/admin/categorias/categorias.component').then(m => m.CategoriasComponent)
  },

  { path: '**', redirectTo: '' }
];
