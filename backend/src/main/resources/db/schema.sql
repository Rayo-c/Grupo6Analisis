-- =====================================================================
-- Sistema de Gestion de Quejas - QuickBite
-- Script de creacion del esquema (PostgreSQL / Neon)
--
-- application.properties usa spring.jpa.hibernate.ddl-auto=validate,
-- es decir Hibernate NO crea ni modifica tablas: solo valida que ya
-- existan con esta estructura exacta. Por eso este script debe
-- ejecutarse UNA VEZ, manualmente, contra la base antes de levantar
-- la aplicacion (por ejemplo desde el SQL Editor de Neon, o con psql).
-- =====================================================================

CREATE TABLE IF NOT EXISTS sucursales (
    id              BIGSERIAL PRIMARY KEY,
    codigo          VARCHAR(20)  NOT NULL UNIQUE,
    nombre          VARCHAR(150) NOT NULL,
    direccion       VARCHAR(250) NOT NULL,
    telefono        VARCHAR(20),
    supervisor_id   BIGINT,          -- FK usuarios.id (rol ROLE_SUPERVISOR), nullable
    estado          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO'  -- RN: ACTIVO / INACTIVO
);

CREATE TABLE IF NOT EXISTS usuarios (
    id                          BIGSERIAL PRIMARY KEY,
    nombre_completo             VARCHAR(150) NOT NULL,
    correo                      VARCHAR(150) NOT NULL UNIQUE,
    password_hash               VARCHAR(255) NOT NULL,
    telefono                    VARCHAR(20),
    rol                         VARCHAR(20)  NOT NULL, -- RN01: ROLE_CLIENTE/ROLE_AGENTE/ROLE_SUPERVISOR/ROLE_ADMIN
    estado                      VARCHAR(30)  NOT NULL DEFAULT 'ACTIVO', -- PENDIENTE_VERIFICACION/ACTIVO/INACTIVO/SUSPENDIDO
    sucursal_id                 BIGINT REFERENCES sucursales(id), -- solo Agente/Supervisor
    token_verificacion          VARCHAR(255),
    token_recuperacion          VARCHAR(255),
    token_recuperacion_expira   TIMESTAMP,
    intentos_fallidos           INT NOT NULL DEFAULT 0,
    motivo_suspension           VARCHAR(500),
    fecha_creacion              TIMESTAMP NOT NULL DEFAULT now()
);

ALTER TABLE sucursales
    ADD CONSTRAINT fk_sucursal_supervisor FOREIGN KEY (supervisor_id) REFERENCES usuarios(id);

CREATE TABLE IF NOT EXISTS categorias_queja (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(500),
    sla_horas   INT NOT NULL,       -- RN05
    estado      VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS quejas (
    id                  BIGSERIAL PRIMARY KEY,
    numero_seguimiento  VARCHAR(20) NOT NULL UNIQUE,
    cliente_id          BIGINT REFERENCES usuarios(id),         -- nulo si es invitado (CU07 - FA01)
    invitado_nombre     VARCHAR(150),
    invitado_correo     VARCHAR(150),
    invitado_telefono   VARCHAR(20),
    sucursal_id         BIGINT NOT NULL REFERENCES sucursales(id),
    categoria_id        BIGINT NOT NULL REFERENCES categorias_queja(id),
    fecha_hora_incidente TIMESTAMP NOT NULL,
    descripcion         VARCHAR(2000) NOT NULL,
    evidencia_url       VARCHAR(500),
    estado              VARCHAR(20) NOT NULL DEFAULT 'REGISTRADA', -- RN02
    agente_id           BIGINT REFERENCES usuarios(id),
    numero_resolucion   INT NOT NULL DEFAULT 0,                    -- RN08
    solucion            VARCHAR(2000),
    tipo_resolucion     VARCHAR(20),
    motivo_escalamiento VARCHAR(1000),
    nivel_escalamiento  VARCHAR(30),
    motivo_reapertura   VARCHAR(1000),
    veces_reabierta     INT NOT NULL DEFAULT 0,
    fecha_registro      TIMESTAMP NOT NULL DEFAULT now(),
    fecha_asignacion    TIMESTAMP,
    fecha_resolucion    TIMESTAMP,
    fecha_cierre        TIMESTAMP
);

CREATE TABLE IF NOT EXISTS seguimientos_queja (
    id            BIGSERIAL PRIMARY KEY,
    queja_id      BIGINT NOT NULL REFERENCES quejas(id) ON DELETE CASCADE,
    usuario_id    BIGINT NOT NULL REFERENCES usuarios(id),
    comentario    VARCHAR(2000) NOT NULL,
    evidencia_url VARCHAR(500),
    fecha         TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS calificaciones_queja (
    id                BIGSERIAL PRIMARY KEY,
    queja_id          BIGINT NOT NULL REFERENCES quejas(id) ON DELETE CASCADE,
    numero_resolucion INT NOT NULL,               -- RN08: una calificacion por resolucion
    calificacion      INT NOT NULL CHECK (calificacion BETWEEN 1 AND 5),
    comentario        VARCHAR(1000),
    fecha             TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (queja_id, numero_resolucion)
);

CREATE TABLE IF NOT EXISTS bitacora_notificaciones (
    id            BIGSERIAL PRIMARY KEY,
    destinatario  VARCHAR(150) NOT NULL,
    tipo          VARCHAR(40)  NOT NULL,          -- RN07
    estado        VARCHAR(20)  NOT NULL,          -- ENVIADO / FALLIDO / REINTENTANDO
    queja_id      BIGINT,
    detalle       VARCHAR(500),
    intentos      INT NOT NULL DEFAULT 1,
    fecha         TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_quejas_estado ON quejas(estado);
CREATE INDEX IF NOT EXISTS idx_quejas_sucursal ON quejas(sucursal_id);
CREATE INDEX IF NOT EXISTS idx_quejas_agente ON quejas(agente_id);
CREATE INDEX IF NOT EXISTS idx_usuarios_rol_estado_sucursal ON usuarios(rol, estado, sucursal_id);

-- =====================================================================
-- Datos semilla minimos para poder iniciar sesion como Administrador
-- Correo: admin@quickbite.com
-- Password: Admin#2026  (hash BCrypt real, generado y verificado)
-- CAMBIAR esta contrasena despues del primer inicio de sesion.
-- =====================================================================
INSERT INTO usuarios (nombre_completo, correo, password_hash, rol, estado)
VALUES ('Administrador General', 'admin@quickbite.com',
        '$2b$10$yAoLoeEHu/xWbooYKkAfouHY7hCKU1SXPtwn/PNqlGfdl/dg.Gvdm',
        'ROLE_ADMIN', 'ACTIVO')
ON CONFLICT (correo) DO NOTHING;
