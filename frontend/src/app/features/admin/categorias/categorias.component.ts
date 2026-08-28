import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CategoriaService } from '../../../core/services/categoria.service';
import { CategoriaResponse } from '../../../core/models/categoria.model';

/** CU06 - Administrar Categorias de Queja. RN03. */
@Component({
  selector: 'app-categorias',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './categorias.component.html'
})
export class CategoriasComponent implements OnInit {
  categorias: CategoriaResponse[] = [];
  mostrarFormulario = false;
  editandoId: number | null = null;
  errorMsg = '';
  mensajeExito = '';

  form = this.fb.group({
    nombre: ['', Validators.required],
    descripcion: [''],
    slaHoras: [24, [Validators.required, Validators.min(1)]]
  });

  constructor(private fb: FormBuilder, private categoriaService: CategoriaService) {}

  ngOnInit(): void {
    this.buscar();
  }

  buscar(): void {
    this.categoriaService.listar().subscribe({
      next: (data) => (this.categorias = data),
      error: (err) => (this.errorMsg = err.message)
    });
  }

  abrirCrear(): void {
    this.editandoId = null;
    this.form.reset({ slaHoras: 24 });
    this.mostrarFormulario = true;
  }

  abrirEditar(c: CategoriaResponse): void {
    this.editandoId = c.id;
    this.form.setValue({ nombre: c.nombre, descripcion: c.descripcion || '', slaHoras: c.slaHoras });
    this.mostrarFormulario = true;
  }

  guardar(): void {
    if (this.form.invalid) return;
    const request = this.form.getRawValue() as any;
    this.errorMsg = '';

    const llamada = this.editandoId
      ? this.categoriaService.actualizar(this.editandoId, request)
      : this.categoriaService.crear(request);

    llamada.subscribe({
      next: () => {
        this.mensajeExito = this.editandoId ? 'Categoria de queja actualizada exitosamente.' : 'Categoria de queja creada exitosamente.';
        this.mostrarFormulario = false;
        this.buscar();
      },
      error: (err) => (this.errorMsg = err.message)
    });
  }

  cambiarEstado(c: CategoriaResponse): void {
    this.categoriaService.cambiarEstado(c.id).subscribe({
      next: () => this.buscar(),
      error: (err) => (this.errorMsg = err.message)
    });
  }
}
