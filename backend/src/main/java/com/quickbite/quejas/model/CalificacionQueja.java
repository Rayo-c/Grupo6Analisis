package com.quickbite.quejas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * CU14 - Calificar Resolucion de Queja.
 * RN08: una calificacion independiente por cada resolucion (numeroResolucion).
 */
@Entity
@Table(name = "calificaciones_queja",
        uniqueConstraints = @UniqueConstraint(columnNames = {"queja_id", "numero_resolucion"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalificacionQueja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "queja_id", nullable = false)
    private Queja queja;

    @Column(name = "numero_resolucion", nullable = false)
    private Integer numeroResolucion;

    @Column(nullable = false)
    private Integer calificacion; // 1 a 5

    @Column(length = 1000)
    private String comentario;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fecha = LocalDateTime.now();
}
