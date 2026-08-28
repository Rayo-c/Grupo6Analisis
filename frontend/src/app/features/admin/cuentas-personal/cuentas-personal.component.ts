import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, FormsModule, Validators } from '@angular/forms';
import { UsuarioService } from '../../../core/services/usuario.service';
import { SucursalService } from '../../../core/services/sucursal.service';
import { UsuarioResponse, Rol } from '../../../core/models/usuario.model';
import { SucursalResponse } from '../../../core/models/sucursal.model';

/**
 * CU04 - Administrar Cuentas de Usuario (Personal Interno).
 * El administrador SOLO crea/edita cuentas de Agente, Supervisor y Administrador.
 * Las cuentas de Cliente se gestionan aparte (ver CuentasClientesComponent), en modo consulta.
 */
@Component({
  selector: 'app-cuentas-personal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './cuentas-personal.component.html'
})
export class CuentasPersonalComponent implements OnInit {
  personal: UsuarioResponse[] = [];
  sucursales: SucursalResponse[] = [];
  mostrarFormulario = false;
  editandoId: number | null = null;
  errorMsg = '';
  mensajeExito = '';

  readonly roles: Rol[] = ['ROLE_AGENTE', 'ROLE_SUPERVISOR', 'ROLE_ADMIN'];

  filtroTexto = '';
  filtroRol = '';
  filtroEstado = '';

  form = this.fb.group({
    nombreCompleto: ['', Validators.required],
    correo: ['', [Validators.required, Validators.email]],
    rol: ['ROLE_AGENTE' as Rol, Validators.required],
    sucursalId: [null as number | null]
  });

  constructor(
    private fb: FormBuilder,
    private usuarioService: UsuarioService,
    private sucursalService: SucursalService
  ) {}

  ngOnInit(): void {
    this.sucursalService.listar().subscribe((data) => (this.sucursales = data));
    this.buscar();
  }

  buscar(): void {
    this.usuarioService.listarPersonal(this.filtroTexto, this.filtroRol, this.filtroEstado)
      .subscribe({
        next: (data) => (this.personal = data),
        error: (err) => (this.errorMsg = err.message)
      });
  }

  abrirCrear(): void {
    this.editandoId = null;
    this.form.reset({ rol: 'ROLE_AGENTE', sucursalId: null });
    this.mostrarFormulario = true;
  }

  abrirEditar(u: UsuarioResponse): void {
    this.editandoId = u.id;
    this.form.setValue({
      nombreCompleto: u.nombreCompleto,
      correo: u.correo,
      rol: u.rol,
      sucursalId: u.sucursalId ?? null
    });
    this.mostrarFormulario = true;
  }

  guardar(): void {
    if (this.form.invalid) return;
    const request = this.form.getRawValue() as any;
    this.errorMsg = '';

    const llamada = this.editandoId
      ? this.usuarioService.actualizarPersonal(this.editandoId, request)
      : this.usuarioService.crearPersonal(request);

    llamada.subscribe({
      next: () => {
        this.mensajeExito = this.editandoId ? 'Cuenta actualizada exitosamente.' : 'Cuenta creada exitosamente.';
        this.mostrarFormulario = false;
        this.buscar();
      },
      error: (err) => (this.errorMsg = err.message)
    });
  }

  cambiarEstado(u: UsuarioResponse): void {
    this.usuarioService.cambiarEstadoPersonal(u.id).subscribe({
      next: () => this.buscar(),
      error: (err) => (this.errorMsg = err.message)
    });
  }
}
