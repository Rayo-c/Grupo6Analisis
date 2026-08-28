package com.quickbite.quejas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad central del sistema.
 * CU07 Registrar Queja, CU08 Administrar Quejas, CU09 Asignar Queja,
 * CU10 Dar Seguimiento, CU11 Resolver, CU12 Escalar, CU13 Reabrir.
 */
@Entity
@Table(name = "quejas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Queja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_seguimiento", nullable = false, unique = true, length = 20)
    private String numeroSeguimiento;

    /** Nulo si la queja fue registrada como invitado (CU07 - FA01). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Usuario cliente;

    @Column(name = "invitado_nombre", length = 150)
    private String invitadoNombre;

    @Column(name = "invitado_correo", length = 150)
    private String invitadoCorreo;

    @Column(name = "invitado_telefono", length = 20)
    private String invitadoTelefono;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaQueja categoria;

    @Column(name = "fecha_hora_incidente", nullable = false)
    private LocalDateTime fechaHoraIncidente;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    @Column(name = "evidencia_url", length = 500)
    private String evidenciaUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoQueja estado = EstadoQueja.REGISTRADA;

    /** Agente responsable actual (RN09 - asignacion automatica y aleatoria). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agente_id")
    private Usuario agenteAsignado;

    /** Contador de resoluciones (RN08 - una calificacion independiente por cada resolucion). */
    @Column(name = "numero_resolucion", nullable = false)
    @Builder.Default
    private Integer numeroResolucion = 0;

    @Column(name = "solucion", length = 2000)
    private String solucion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_resolucion", length = 20)
    private TipoResolucion tipoResolucion;

    @Column(name = "motivo_escalamiento", length = 1000)
    private String motivoEscalamiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_escalamiento", length = 30)
    private NivelEscalamiento nivelEscalamiento;

    @Column(name = "motivo_reapertura", length = 1000)
    private String motivoReapertura;

    @Column(name = "veces_reabierta", nullable = false)
    @Builder.Default
    private Integer vecesReabierta = 0;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @OneToMany(mappedBy = "queja", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SeguimientoQueja> historialSeguimiento = new ArrayList<>();

    @OneToMany(mappedBy = "queja", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CalificacionQueja> calificaciones = new ArrayList<>();

    @Transient
    public boolean esInvitado() {
        return cliente == null;
    }

    @Transient
    public String correoContacto() {
        return cliente != null ? cliente.getCorreo() : invitadoCorreo;
    }

    @Transient
    public String nombreContacto() {
        return cliente != null ? cliente.getNombreCompleto() : invitadoNombre;
    }
}
