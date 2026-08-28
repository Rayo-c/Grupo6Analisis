package com.quickbite.quejas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** CU06 - Administrar Categorias de Queja. RN03. */
@Entity
@Table(name = "categorias_queja")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaQueja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    /** SLA - RN05 / usado por CU12 (escalamiento automatico). */
    @Column(name = "sla_horas", nullable = false)
    private Integer slaHoras;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoGeneral estado = EstadoGeneral.ACTIVO;
}
