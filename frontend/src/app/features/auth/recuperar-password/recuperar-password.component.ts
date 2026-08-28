import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

/** CU03 - Recuperar Contrasena (solicitud + restablecimiento con token). */
@Component({
  selector: 'app-recuperar-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './recuperar-password.component.html'
})
export class RecuperarPasswordComponent {
  private readonly patronPassword = /^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]).{8,}$/;

  pasoActual: 'solicitar' | 'restablecer' = 'solicitar';
  cargando = false;
  errorMsg = '';
  mensajeExito = '';

  formSolicitud = this.fb.group({
    correo: ['', [Validators.required, Validators.email]]
  });

  formReset = this.fb.group({
    token: ['', Validators.required],
    nuevaPassword: ['', [Validators.required, Validators.pattern(this.patronPassword)]]
  });

  constructor(private fb: FormBuilder, private authService: AuthService) {}

  solicitar(): void {
    if (this.formSolicitud.invalid) return;
    this.cargando = true;
    this.errorMsg = '';

    this.authService.recuperarPassword(this.formSolicitud.getRawValue() as any).subscribe({
      next: () => {
        this.cargando = false;
        this.mensajeExito = 'Te enviamos un codigo de recuperacion. Revisa tu correo (valido 30 minutos).';
        this.pasoActual = 'restablecer';
      },
      error: (err) => {
        this.cargando = false;
        this.errorMsg = err.message;
      }
    });
  }

  restablecer(): void {
    if (this.formReset.invalid) return;
    this.cargando = true;
    this.errorMsg = '';

    this.authService.resetPassword(this.formReset.getRawValue() as any).subscribe({
      next: () => {
        this.cargando = false;
        this.mensajeExito = 'Tu contrasena fue actualizada. Ya puedes iniciar sesion.';
      },
      error: (err) => {
        this.cargando = false;
        this.errorMsg = err.message;
      }
    });
  }
}
