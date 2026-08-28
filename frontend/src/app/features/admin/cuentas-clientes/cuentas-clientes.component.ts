import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuarioService } from '../../../core/services/usuario.service';
import { UsuarioResponse } from '../../../core/models/usuario.model';

/**
 * CU04 - Administrar Cuentas de Usuario (vista Clientes).
 * Solo consulta; el administrador unicamente puede cambiar el estado (suspender/activar)
 * con un motivo obligatorio. Las cuentas se crean por autorregistro (CU02), no aqui.
 */
@Component({
  selector: 'app-cuentas-clientes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cuentas-clientes.component.html'
})
export class CuentasClientesComponent implements OnInit {
  clientes: UsuarioResponse[] = [];
  filtroTexto = '';
  filtroEstado = '';
  errorMsg = '';
  mensajeExito = '';

  clienteParaSuspender: UsuarioResponse | null = null;
  motivoSuspension = '';

  constructor(private usuarioService: UsuarioService) {}

  ngOnInit(): void {
    this.buscar();
  }

  buscar(): void {
    this.usuarioService.listarClientes(this.filtroTexto, this.filtroEstado).subscribe({
      next: (data) => (this.clientes = data),
      error: (err) => (this.errorMsg = err.message)
    });
  }

  abrirCambioEstado(c: UsuarioResponse): void {
    this.clienteParaSuspender = c;
    this.motivoSuspension = '';
  }

  confirmarCambioEstado(): void {
    if (!this.clienteParaSuspender || !this.motivoSuspension.trim()) {
      this.errorMsg = 'El campo es obligatorio.';
      return;
    }
    this.usuarioService.cambiarEstadoCliente(this.clienteParaSuspender.id, { motivo: this.motivoSuspension })
      .subscribe({
        next: () => {
          this.mensajeExito = 'Cuenta actualizada exitosamente.';
          this.clienteParaSuspender = null;
          this.buscar();
        },
        error: (err) => (this.errorMsg = err.message)
      });
  }
}
