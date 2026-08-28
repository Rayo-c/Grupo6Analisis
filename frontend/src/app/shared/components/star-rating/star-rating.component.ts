import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

/** CU14 - Calificar Resolucion de Queja: selector de 1 a 5 estrellas. */
@Component({
  selector: 'app-star-rating',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="estrellas">
      @for (n of [1,2,3,4,5]; track n) {
        <span
          class="estrella"
          [class.activa]="n <= (hover || valor)"
          (mouseenter)="hover = n"
          (mouseleave)="hover = 0"
          (click)="seleccionar(n)">&#9733;</span>
      }
    </div>
  `,
  styles: [`
    .estrellas { display: flex; gap: 4px; font-size: 28px; cursor: pointer; }
    .estrella { color: #d9d9d9; transition: color 0.1s; }
    .estrella.activa { color: #f5a623; }
  `]
})
export class StarRatingComponent {
  @Input() valor = 0;
  @Output() valorChange = new EventEmitter<number>();
  hover = 0;

  seleccionar(n: number): void {
    this.valor = n;
    this.valorChange.emit(n);
  }
}
