import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { InfoPortal, PortalService } from '../../core/services/portal.service';
import { AuthService } from '../../core/services/auth.service';

/** CU00 - Portal (Pagina de Inicio). */
@Component({
  selector: 'app-portal-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './portal-home.component.html'
})
export class PortalHomeComponent implements OnInit {
  info: InfoPortal | null = null;
  editando = false;
  borrador: InfoPortal = { mision: '', vision: '', valores: '' };
  esAdmin = false;

  constructor(
    private portalService: PortalService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.portalService.obtenerInfo().subscribe((data) => (this.info = data));
    this.esAdmin = this.detectarAdmin();
  }

private detectarAdmin(): boolean {
  try {
    const token = this.authService.obtenerToken();
    if (!token) return false;
    const payload = JSON.parse(atob(token.split('.')[1]));
    const rol = payload.rol ?? payload.role ?? payload.roles ?? payload.authorities;
    return JSON.stringify(rol).toUpperCase().includes('ADMIN');
  } catch {
    return false;
  }
}

  editar(): void {
    if (this.info) this.borrador = { ...this.info };
    this.editando = true;
  }

  guardar(): void {
    this.portalService.actualizarInfo(this.borrador).subscribe((data) => {
      this.info = data;
      this.editando = false;
    });
  }

  restaurar(): void {
    this.portalService.restaurarInfo().subscribe((data) => {
      this.info = data;
      this.editando = false;
    });
  }
}