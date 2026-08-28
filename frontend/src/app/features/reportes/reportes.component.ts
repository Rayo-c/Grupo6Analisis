import { Component, OnInit } from '@angular/core';
import { CommonModule, KeyValuePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ReporteService } from '../../core/services/reporte.service';
import { SucursalService } from '../../core/services/sucursal.service';
import { CategoriaService } from '../../core/services/categoria.service';
import { ReporteQuejasResponse } from '../../core/models/reporte.model';
import { SucursalResponse } from '../../core/models/sucursal.model';
import { CategoriaResponse } from '../../core/models/categoria.model';

/** CU15 - Generar Reportes de Quejas. */
@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, KeyValuePipe],
  templateUrl: './reportes.component.html'
})
export class ReportesComponent implements OnInit {
  reporte: ReporteQuejasResponse | null = null;
  sucursales: SucursalResponse[] = [];
  categorias: CategoriaResponse[] = [];
  cargando = false;
  errorMsg = '';

  filtros = this.fb.group({
    desde: [''],
    hasta: [''],
    sucursalId: [null as number | null],
    categoriaId: [null as number | null]
  });

  constructor(
    private fb: FormBuilder,
    private reporteService: ReporteService,
    private sucursalService: SucursalService,
    private categoriaService: CategoriaService
  ) {}

  ngOnInit(): void {
    this.sucursalService.listar().subscribe((data) => (this.sucursales = data));
    this.categoriaService.listar().subscribe((data) => (this.categorias = data));
  }

  generar(): void {
    this.cargando = true;
    this.errorMsg = '';
    this.reporte = null;
    const valores = this.filtros.getRawValue();
    const filtrosLimpios = Object.fromEntries(
      Object.entries(valores).filter(([, v]) => v !== null && v !== '')
    );

    this.reporteService.generar(filtrosLimpios).subscribe({
      next: (data) => { this.reporte = data; this.cargando = false; },
      error: (err) => { this.errorMsg = err.message; this.cargando = false; }
    });
  }
}
