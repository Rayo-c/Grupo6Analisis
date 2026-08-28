package com.quickbite.quejas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Usuario unificado del sistema (Cliente, Agente, Supervisor, Administrador).
 * RN01 - Roles.
 * CU01 Iniciar Sesion, CU02 Registrar Cliente, CU03 Recuperar Contrasena, CU04 Administrar Cuentas.
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(length = 20)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EstadoCuenta estado = EstadoCuenta.ACTIVO;

    /** Solo aplica a Agente y Supervisor (RN01). Nulo para Cliente y Administrador. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;

    @Column(name = "token_verificacion")
    private String tokenVerificacion;

    @Column(name = "token_recuperacion")
    private String tokenRecuperacion;

    @Column(name = "token_recuperacion_expira")
    private LocalDateTime tokenRecuperacionExpira;

    @Column(name = "intentos_fallidos", nullable = false)
    @Builder.Default
    private Integer intentosFallidos = 0;

    @Column(name = "motivo_suspension", length = 500)
    private String motivoSuspension;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public boolean esPersonalInterno() {
        return rol == Rol.ROLE_AGENTE || rol == Rol.ROLE_SUPERVISOR || rol == Rol.ROLE_ADMIN;
    }
}
