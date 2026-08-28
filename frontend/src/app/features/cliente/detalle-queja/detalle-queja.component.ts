import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, FormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { QuejaService } from '../../../core/services/queja.service';
import { AuthService } from '../../../core/services/auth.service';
import { QuejaResponse, TipoResolucion, NivelEscalamiento } from '../../../core/models/queja.model';
import { StarRatingComponent } from '../../../shared/components/star-rating/star-rating.component';

/**
 * Vista de detalle unica para Cliente, Agente y Supervisor.
 * Muestra solo las acciones habilitadas segun el estado de la queja y el rol
 * del usuario autenticado (equivalente a CU08 paso 10 y sus "Ver CU ...").
 */
@Component({
  selector: 'app-detalle-queja',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterLink, StarRatingComponent],
  templateUrl: './detalle-queja.component.html'
})
export class DetalleQuejaComponent implements OnInit {
  queja: QuejaResponse | null = null;
  cargando = true;
  errorMsg = '';
  mensajeExito = '';

  panelActivo: 'seguimiento' | 'resolver' | 'escalar' | 'reabrir' | 'calificar' | null = null;

  readonly tiposResolucion: TipoResolucion[] = ['REEMBOLSO', 'REPOSICION', 'DISCULPA', 'OTRO'];
  readonly nivelesEscalamiento: NivelEscalamiento[] = ['SUPERVISOR', 'GERENCIA_REGIONAL'];

  formSeguimiento = this.fb.group({ comentario: ['', Validators.required], evidenciaUrl: [''] });
  formResolver = this.fb.group({
    solucion: ['', Validators.required],
    tipoResolucion: ['REEMBOLSO' as TipoResolucion, Validators.required]
  });
  formEscalar = this.fb.group({
    motivo: ['', Validators.required],
    nivel: ['SUPERVISOR' as NivelEscalamiento, Validators.required]
  });
  formReabrir = this.fb.group({ motivo: ['', Validators.required] });
  calificacionSeleccionada = 0;
  comentarioCalificacion = '';

  private id!: number;

  constructor(
    private route: ActivatedRoute,
    private quejaService: QuejaService,
    public authService: AuthService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.cargar();
  }

  cargar(): void {
    this.cargando = true;
    this.quejaService.detalle(this.id).subscribe({
      next: (data) => { this.queja = data; this.cargando = false; },
      error: (err) => { this.errorMsg = err.message; this.cargando = false; }
    });
  }

  // -------- helpers de visibilidad de acciones --------

  get esCliente(): boolean { return this.authService.tieneRol('ROLE_CLIENTE'); }
  get esAgenteOSupervisor(): boolean { return this.authService.tieneRol('ROLE_AGENTE', 'ROLE_SUPERVISOR'); }

  get puedeDarSeguimiento(): boolean {
    return this.esAgenteOSupervisor && !!this.queja &&
      ['ASIGNADA', 'EN_PROCESO', 'REABIERTA'].includes(this.queja.estado);
  }

  get puedeResolver(): boolean {
    return this.esAgenteOSupervisor && !!this.queja &&
      ['ASIGNADA', 'EN_PROCESO', 'REABIERTA'].includes(this.queja.estado);
  }

  get puedeEscalar(): boolean {
    return this.esAgenteOSupervisor && !!this.queja &&
      ['ASIGNADA', 'EN_PROCESO'].includes(this.queja.estado);
  }

  // CU13 - dentro del plazo (validado tambien en backend) y no reabierta antes
  get puedeReabrir(): boolean {
    return this.esCliente && !!this.queja && this.queja.estado === 'RESUELTA' && this.queja.vecesReabierta === 0;
  }

  // CU14 - RN08: una calificacion por resolucion actual
  get puedeCalificar(): boolean {
    if (!this.esCliente || !this.queja || this.queja.estado !== 'RESUELTA') return false;
    return !this.queja.calificaciones.some(c => c.numeroResolucion === this.queja!.numeroResolucion);
  }

  togglePanel(panel: typeof this.panelActivo): void {
    this.panelActivo = this.panelActivo === panel ? null : panel;
    this.mensajeExito = '';
    this.errorMsg = '';
  }

  // -------- acciones --------

  enviarSeguimiento(): void {
    if (this.formSeguimiento.invalid) return;
    this.quejaService.agregarSeguimiento(this.id, this.formSeguimiento.getRawValue() as any).subscribe({
      next: (data) => this.onExito(data, 'Seguimiento agregado exitosamente.'),
      error: (err) => (this.errorMsg = err.message)
    });
  }

  enviarResolucion(): void {
    if (this.formResolver.invalid) return;
    this.quejaService.resolver(this.id, this.formResolver.getRawValue() as any).subscribe({
      next: (data) => this.onExito(data, 'Queja marcada como resuelta exitosamente.'),
      error: (err) => (this.errorMsg = err.message)
    });
  }

  enviarEscalamiento(): void {
    if (this.formEscalar.invalid) return;
    this.quejaService.escalar(this.id, this.formEscalar.getRawValue() as any).subscribe({
      next: (data) => this.onExito(data, 'Queja escalada exitosamente.'),
      error: (err) => (this.errorMsg = err.message)
    });
  }

  enviarReapertura(): void {
    if (this.formReabrir.invalid) return;
    this.quejaService.reabrir(this.id, this.formReabrir.getRawValue() as any).subscribe({
      next: (data) => this.onExito(data, 'Queja reabierta exitosamente.'),
      error: (err) => (this.errorMsg = err.message)
    });
  }

  enviarCalificacion(): void {
    if (this.calificacionSeleccionada < 1) {
      this.errorMsg = 'Debe seleccionar una calificacion.';
      return;
    }
    this.quejaService.calificar(this.id, {
      calificacion: this.calificacionSeleccionada,
      comentario: this.comentarioCalificacion || undefined
    }).subscribe({
      next: (data) => this.onExito(data, 'Gracias por tu opinion.'),
      error: (err) => (this.errorMsg = err.message)
    });
  }

  private onExito(data: QuejaResponse, mensaje: string): void {
    this.queja = data;
    this.mensajeExito = mensaje;
    this.errorMsg = '';
    this.panelActivo = null;
    this.formSeguimiento.reset();
    this.formResolver.reset({ tipoResolucion: 'REEMBOLSO' });
    this.formEscalar.reset({ nivel: 'SUPERVISOR' });
    this.formReabrir.reset();
    this.calificacionSeleccionada = 0;
    this.comentarioCalificacion = '';
  }
}
