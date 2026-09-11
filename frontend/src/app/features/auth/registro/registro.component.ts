import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

/** CU02 - Registrar Cliente (autorregistro). RN06 - password: min 8, mayuscula, numero, especial. */
@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './registro.component.html'
})
export class RegistroComponent {
  private readonly patronPassword = /^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]).{8,}$/;
  private readonly patronTelefono = /^[0-9]{8}$/;

  form = this.fb.group({
    nombreCompleto: ['', Validators.required],
    correo: ['', [Validators.required, Validators.email]],
    telefono: ['', [Validators.pattern(this.patronTelefono)]],
    password: ['', [Validators.required, Validators.pattern(this.patronPassword)]],
    confirmarPassword: ['', Validators.required]
  });

  cargando = false;
  errorMsg = '';
  registroExitoso = false;

  constructor(private fb: FormBuilder, private authService: AuthService) {}

  /** Bloquea cualquier tecla que no sea un digito, mientras el usuario escribe. */
  soloNumeros(event: KeyboardEvent): void {
    const permitidas = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab'];
    if (permitidas.includes(event.key)) return;
    if (!/^[0-9]$/.test(event.key)) {
      event.preventDefault();
    }
  }

  enviar(): void {
    if (this.form.invalid) return;

    const { password, confirmarPassword } = this.form.getRawValue();
    if (password !== confirmarPassword) {
      this.errorMsg = 'Las contrasenas no coinciden.';
      return;
    }

    this.cargando = true;
    this.errorMsg = '';

    const { confirmarPassword: _omit, ...request } = this.form.getRawValue();
    this.authService.registrarCliente(request as any).subscribe({
      next: () => {
        this.cargando = false;
        this.registroExitoso = true; // CU02 - paso 9: mensaje + verificacion por correo
      },
      error: (err) => {
        this.cargando = false;
        this.errorMsg = err.message;
      }
    });
  }
}