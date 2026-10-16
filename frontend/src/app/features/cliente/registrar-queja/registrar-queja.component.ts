import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { QuejaService } from '../../../core/services/queja.service';
import { SucursalService } from '../../../core/services/sucursal.service';
import { CategoriaService } from '../../../core/services/categoria.service';
import { AuthService } from '../../../core/services/auth.service';
import { SucursalResponse } from '../../../core/models/sucursal.model';
import { CategoriaResponse } from '../../../core/models/categoria.model';

/** Valida que la fecha/hora del incidente no sea posterior a la fecha/hora actual (hora local del navegador). */
function noFuturo(control: AbstractControl): ValidationErrors | null {
  if (!control.value) return null;
  return new Date(control.value).getTime() > Date.now() ? { futuro: true } : null;
}

/** Fecha/hora local actual en formato yyyy-MM-ddTHH:mm (valor valido para datetime-local). */
function ahoraLocal(): string {
  const d = new Date();
  d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
  return d.toISOString().slice(0, 16);
}

/**
 * CU07 - Registrar Queja.
 * FA01: si no hay sesion activa, se completan los datos de invitado y se registra igual.
 */
@Component({
  selector: 'app-registrar-queja',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './registrar-queja.component.html'
})
export class RegistrarQuejaComponent implements OnInit {
  sucursales: SucursalResponse[] = [];
  categorias: CategoriaResponse[] = [];
  cargando = false;
  errorMsg = '';
  numeroSeguimiento = '';
  maxFechaHora = ahoraLocal();

  form = this.fb.group({
    sucursalId: [null as number | null, Validators.required],
    categoriaId: [null as number | null, Validators.required],
    fechaHoraIncidente: ['', [Validators.required, noFuturo]],
    descripcion: ['', Validators.required],
    evidenciaUrl: [''],
    invitadoNombre: [''],
    invitadoCorreo: [''],
    invitadoTelefono: ['']
  });

  constructor(
      private fb: FormBuilder,
      private quejaService: QuejaService,
      private sucursalService: SucursalService,
      private categoriaService: CategoriaService,
      public authService: AuthService,
      private router: Router
  ) {}

  ngOnInit(): void {
    this.sucursalService.listar().subscribe((data) => (this.sucursales = data.filter(s => s.estado === 'ACTIVO')));
    this.categoriaService.listar().subscribe((data) => (this.categorias = data.filter(c => c.estado === 'ACTIVO')));

    if (!this.authService.estaAutenticado()) {
      this.form.get('invitadoNombre')?.addValidators(Validators.required);
      this.form.get('invitadoCorreo')?.addValidators([Validators.required, Validators.email]);
    }
  }

  enviar(): void {
    this.maxFechaHora = ahoraLocal();
    this.form.get('fechaHoraIncidente')?.updateValueAndValidity();
    if (this.form.invalid) return;
    this.cargando = true;
    this.errorMsg = '';

    const request = this.form.getRawValue() as any;
    const llamada = this.authService.estaAutenticado()
        ? this.quejaService.registrar(request)
        : this.quejaService.registrarComoInvitado(request);

    llamada.subscribe({
      next: (resp) => {
        this.cargando = false;
        this.numeroSeguimiento = resp.numeroSeguimiento; // CU07 - paso 14
      },
      error: (err) => {
        this.cargando = false;
        this.errorMsg = err.message;
      }
    });
  }
}