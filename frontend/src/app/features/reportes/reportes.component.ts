import { Component, OnInit } from '@angular/core';
import { CommonModule, KeyValuePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ReporteService } from '../../core/services/reporte.service';
import { SucursalService } from '../../core/services/sucursal.service';
import { CategoriaService } from '../../core/services/categoria.service';
import { ReporteQuejasResponse } from '../../core/models/reporte.model';
import { SucursalResponse } from '../../core/models/sucursal.model';
import { CategoriaResponse } from '../../core/models/categoria.model';
import { exportarReporteExcel, exportarReportePdf } from '../../core/utils/reporte-export';

export type TipoReporte = 'CATEGORIA' | 'SUCURSAL' | 'TIEMPO' | 'SATISFACCION';

interface Barra { nombre: string; valor: number; pct: number; color?: string; }

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
  mensajeExito = '';
  filtrosAplicados = '';
  exportando = false;

  /** CU15 paso 4: tipo de reporte. */
  readonly tipos: { valor: TipoReporte; etiqueta: string }[] = [
    { valor: 'CATEGORIA', etiqueta: 'Quejas por Categoria' },
    { valor: 'SUCURSAL', etiqueta: 'Quejas por Sucursal' },
    { valor: 'TIEMPO', etiqueta: 'Tiempo Promedio de Resolucion' },
    { valor: 'SATISFACCION', etiqueta: 'Indice de Satisfaccion' }
  ];
  tipoAplicado: TipoReporte = 'CATEGORIA';

  // Datos de las graficas (se calculan al generar el reporte)
  barrasCategoria: Barra[] = [];
  barrasSucursal: Barra[] = [];
  barrasEstado: Barra[] = [];
  barrasTiempo: Barra[] = [];
  barrasAgentes: Barra[] = [];
  readonly estados = ['REGISTRADA', 'ASIGNADA', 'EN_PROCESO', 'ESCALADA', 'RESUELTA', 'CERRADA', 'REABIERTA'];

  filtros = this.fb.group({
    desde: [''],
    hasta: [''],
    sucursalId: [null as number | null],
    categoriaId: [null as number | null],
    estado: [''],
    tipo: ['CATEGORIA' as TipoReporte]
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
    this.mensajeExito = '';
    this.reporte = null;
    const { tipo, ...valores } = this.filtros.getRawValue();
    const filtrosLimpios = Object.fromEntries(
        Object.entries(valores).filter(([, v]) => v !== null && v !== '')
    );

    this.reporteService.generar(filtrosLimpios).subscribe({
      next: (data) => {
        this.reporte = data;
        this.tipoAplicado = tipo ?? 'CATEGORIA';
        this.construirBarras(data);
        this.cargando = false;
        this.mensajeExito = 'Reporte generado exitosamente.'; // AN01 No.16
        this.filtrosAplicados = this.describirFiltros();
      },
      error: (err) => { this.errorMsg = err.message; this.cargando = false; }
    });
  }

  exportarPdf(): void { this.exportar(exportarReportePdf); }

  exportarExcel(): void { this.exportar(exportarReporteExcel); }

  private exportar(fn: (r: ReporteQuejasResponse, filtros: string) => Promise<void>): void {
    if (!this.reporte) return;
    this.exportando = true;
    this.errorMsg = '';
    fn(this.reporte, this.filtrosAplicados)
        .catch(() => (this.errorMsg = 'No fue posible exportar el reporte. Intente nuevamente.'))
        .finally(() => (this.exportando = false));
  }

  private describirFiltros(): string {
    const v = this.filtros.getRawValue();
    const suc = this.sucursales.find((s) => s.id === v.sucursalId)?.nombre ?? 'Todas';
    const cat = this.categorias.find((c) => c.id === v.categoriaId)?.nombre ?? 'Todas';
    const tipoTxt = this.tipos.find((x) => x.valor === v.tipo)?.etiqueta ?? '';
    return `Reporte: ${tipoTxt} | Desde: ${v.desde || '-'} | Hasta: ${v.hasta || '-'} | Sucursal: ${suc} | Categoria: ${cat} | Estado: ${v.estado || 'Todos'}`;
  }

  private barras(mapa: Record<string, number>): Barra[] {
    const items = Object.entries(mapa).sort((a, b) => b[1] - a[1]);
    const max = Math.max(...items.map(([, v]) => v), 1);
    return items.map(([nombre, valor]) => ({ nombre, valor, pct: (valor / max) * 100 }));
  }

  private construirBarras(r: ReporteQuejasResponse): void {
    this.barrasCategoria = this.barras(r.porCategoria);
    this.barrasSucursal = this.barras(r.porSucursal);
    this.barrasEstado = this.barras(r.porEstado);

    // Tiempo promedio real frente al SLA definido por cada categoria (horas)
    const referencia = [
      { nombre: 'Promedio real', valor: r.tiempoPromedioResolucionHoras, color: '#d62828' },
      ...this.categorias.map((c) => ({ nombre: 'SLA ' + c.nombre, valor: c.slaHoras, color: '#003049' }))
    ];
    const maxHoras = Math.max(...referencia.map((x) => x.valor), 1);
    this.barrasTiempo = referencia.map((x) => ({ ...x, pct: (x.valor / maxHoras) * 100 }));

    // Calificacion promedio por agente (escala 1-5)
    this.barrasAgentes = r.desempenoPorAgente.map((a) => ({
      nombre: a.agenteNombre,
      valor: a.calificacionPromedio,
      pct: (a.calificacionPromedio / 5) * 100
    }));
  }
}