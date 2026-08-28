import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { QuejaService } from '../../../core/services/queja.service';
import { QuejaResponse } from '../../../core/models/queja.model';

/** CU08 - Administrar Quejas (vista Cliente: "Mis Quejas"). */
@Component({
  selector: 'app-mis-quejas',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './mis-quejas.component.html'
})
export class MisQuejasComponent implements OnInit {
  quejas: QuejaResponse[] = [];
  cargando = true;
  errorMsg = '';

  constructor(private quejaService: QuejaService) {}

  ngOnInit(): void {
    this.quejaService.listar({}).subscribe({
      next: (data) => { this.quejas = data; this.cargando = false; },
      error: (err) => { this.errorMsg = err.message; this.cargando = false; }
    });
  }
}
