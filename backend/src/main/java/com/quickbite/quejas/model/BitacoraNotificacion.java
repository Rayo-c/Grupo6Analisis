package com.quickbite.quejas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** CU16 - Comunicacion Web Service. Bitacora de notificaciones enviadas. */
@Entity
@Table(name = "bitacora_notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BitacoraNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String destinatario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoNotificacion tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoNotificacion estado;

    @Column(name = "queja_id")
    private Long quejaId;

    @Column(length = 500)
    private String detalle;

    @Column(name = "intentos", nullable = false)
    @Builder.Default
    private Integer intentos = 1;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fecha = LocalDateTime.now();
}
