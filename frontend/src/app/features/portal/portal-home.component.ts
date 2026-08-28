import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PortalService } from '../../core/services/portal.service';

/** CU00 - Portal (Pagina de Inicio). */
@Component({
  selector: 'app-portal-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './portal-home.component.html'
})
export class PortalHomeComponent implements OnInit {
  info: { mision: string; vision: string; valores: string } | null = null;

  constructor(private portalService: PortalService) {}

  ngOnInit(): void {
    this.portalService.obtenerInfo().subscribe((data) => (this.info = data));
  }
}
