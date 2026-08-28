import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { SucursalService } from '../../../core/services/sucursal.service';
import { UsuarioService } from '../../../core/services/usuario.service';
import { SucursalResponse } from '../../../core/models/sucursal.model';
import { UsuarioResponse } from '../../../core/models/usuario.model';

/** CU05 - Administrar Sucursales. */
@Component({
  selector: 'app-sucursales',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './sucursales.component.html'
})
export class SucursalesComponent implements OnInit {
  sucursales: SucursalResponse[] = [];
  supervisores: UsuarioResponse[] = [];
  mostrarFormulario = false;
  editandoId: number | null = null;
  errorMsg = '';
  mensajeExito = '';

  form = this.fb.group({
    codigo: ['', Validators.required],
    nombre: ['', Validators.required],
    direccion: ['', Validators.required],
    telefono: [''],
    supervisorId: [null as number | null]
  });

  constructor(
    private fb: FormBuilder,
    private sucursalService: SucursalService,
    private usuarioService: UsuarioService
  ) {}

  ngOnInit(): void {
    this.buscar();
    this.usuarioService.listarPersonal('', 'ROLE_SUPERVISOR', 'ACTIVO').subscribe((data) => (this.supervisores = data));
  }

  buscar(): void {
    this.sucursalService.listar().subscribe({
      next: (data) => (this.sucursales = data),
      error: (err) => (this.errorMsg = err.message)
    });
  }

  abrirCrear(): void {
    this.editandoId = null;
    this.form.reset();
    this.mostrarFormulario = true;
  }

  abrirEditar(s: SucursalResponse): void {
    this.editandoId = s.id;
    this.form.setValue({
      codigo: s.codigo, nombre: s.nombre, direccion: s.direccion,
      telefono: s.telefono || '', supervisorId: s.supervisorId ?? null
    });
    this.mostrarFormulario = true;
  }

  guardar(): void {
    if (this.form.invalid) return;
    const request = this.form.getRawValue() as any;
    this.errorMsg = '';

    const llamada = this.editandoId
      ? this.sucursalService.actualizar(this.editandoId, request)
      : this.sucursalService.crear(request);

    llamada.subscribe({
      next: () => {
        this.mensajeExito = this.editandoId ? 'Sucursal actualizada exitosamente.' : 'Sucursal creada exitosamente.';
        this.mostrarFormulario = false;
        this.buscar();
      },
      error: (err) => (this.errorMsg = err.message)
    });
  }

  cambiarEstado(s: SucursalResponse): void {
    this.sucursalService.cambiarEstado(s.id).subscribe({
      next: () => this.buscar(),
      error: (err) => (this.errorMsg = err.message)
    });
  }
}
