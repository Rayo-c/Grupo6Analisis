package com.quickbite.quejas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * CU10 - Dar Seguimiento a Queja.
 * Historial cronologico e inmutable (no se permite edicion ni eliminacion posterior).
 */
@Entity
@Table(name = "seguimientos_queja")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeguimientoQueja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "queja_id", nullable = false)
    private Queja queja;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 2000)
    private String comentario;

    @Column(name = "evidencia_url", length = 500)
    private String evidenciaUrl;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fecha = LocalDateTime.now();
}
