import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

/** CU01 - Iniciar Sesion. */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  form = this.fb.group({
    correo: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  cargando = false;
  errorMsg = '';

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {}

  enviar(): void {
    if (this.form.invalid) return;
    this.cargando = true;
    this.errorMsg = '';

    this.authService.login(this.form.getRawValue() as any).subscribe({
      next: (resp) => {
        this.cargando = false;
        // CU01 - paso 7: redirige segun el rol
        switch (resp.rol) {
          case 'ROLE_CLIENTE': this.router.navigate(['/mis-quejas']); break;
          case 'ROLE_AGENTE':
          case 'ROLE_SUPERVISOR': this.router.navigate(['/bandeja-quejas']); break;
          case 'ROLE_ADMIN': this.router.navigate(['/admin/cuentas-personal']); break;
          default: this.router.navigate(['/']);
        }
      },
      error: (err) => {
        this.cargando = false;
        this.errorMsg = err.message;
      }
    });
  }
}
