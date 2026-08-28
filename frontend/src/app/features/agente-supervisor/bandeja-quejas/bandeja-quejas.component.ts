import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { QuejaService } from '../../../core/services/queja.service';
import { SucursalService } from '../../../core/services/sucursal.service';
import { CategoriaService } from '../../../core/services/categoria.service';
import { QuejaResponse } from '../../../core/models/queja.model';
import { SucursalResponse } from '../../../core/models/sucursal.model';
import { CategoriaResponse } from '../../../core/models/categoria.model';

/** CU08 - Administrar Quejas (vista Agente / Supervisor, con filtros). */
@Component({
  selector: 'app-bandeja-quejas',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './bandeja-quejas.component.html'
})
export class BandejaQuejasComponent implements OnInit {
  quejas: QuejaResponse[] = [];
  sucursales: SucursalResponse[] = [];
  categorias: CategoriaResponse[] = [];
  cargando = true;
  errorMsg = '';

  readonly estados = ['REGISTRADA', 'ASIGNADA', 'EN_PROCESO', 'ESCALADA', 'RESUELTA', 'CERRADA', 'REABIERTA'];

  filtros = this.fb.group({
    numeroSeguimiento: [''],
    sucursalId: [null as number | null],
    categoriaId: [null as number | null],
    estado: [''],
    desde: [''],
    hasta: ['']
  });

  constructor(
    private fb: FormBuilder,
    private quejaService: QuejaService,
    private sucursalService: SucursalService,
    private categoriaService: CategoriaService
  ) {}

  ngOnInit(): void {
    this.sucursalService.listar().subscribe((data) => (this.sucursales = data));
    this.categoriaService.listar().subscribe((data) => (this.categorias = data));
    this.buscar();
  }

  buscar(): void {
    this.cargando = true;
    this.errorMsg = '';
    const valores = this.filtros.getRawValue();
    const filtrosLimpios = Object.fromEntries(
      Object.entries(valores).filter(([, v]) => v !== null && v !== '')
    );

    this.quejaService.listar(filtrosLimpios).subscribe({
      next: (data) => { this.quejas = data; this.cargando = false; },
      error: (err) => { this.errorMsg = err.message; this.cargando = false; }
    });
  }

  limpiarFiltros(): void {
    this.filtros.reset({ numeroSeguimiento: '', sucursalId: null, categoriaId: null, estado: '', desde: '', hasta: '' });
    this.buscar();
  }
}
