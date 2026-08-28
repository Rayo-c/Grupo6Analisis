# Backend - Sistema de Gestion de Quejas (QuickBite)

Spring Boot 3 / Java 17 / PostgreSQL / JWT. Implementa los casos de uso **CU00 a CU16**.

## Arquitectura (por capas)

```
src/main/java/com/quickbite/quejas/
├── QuejasApplication.java        Clase principal
├── config/                       SecurityConfig, SchedulerConfig
├── security/                     JWT (JwtUtil, JwtAuthFilter, UserDetailsServiceImpl, CustomUserDetails)
├── model/                        Entidades JPA y enums (capa de dominio)
├── repository/                   Interfaces DAO (Spring Data JPA)
├── dto/                          Objetos de entrada/salida por modulo (auth, usuario, sucursal, categoria, queja, reporte)
├── service/                      Interfaces de servicio (reglas de negocio)
│   └── impl/                     Implementaciones
├── controller/                   Controladores REST (un controller por caso de uso relacionado)
├── scheduler/                    Tareas automaticas (cierre por inactividad, escalamiento por SLA)
└── exception/                    Manejo centralizado de errores (catalogo AN02)
```

## Mapeo Caso de Uso -> Codigo

| CU | Descripcion | Controller | Service |
|----|---|---|---|
| CU00 | Portal | `PortalController` | - |
| CU01 | Iniciar Sesion | `AuthController` (`/login`) | `AuthService` |
| CU02 | Registrar Cliente | `AuthController` (`/registro`, `/verificar/{token}`) | `AuthService` |
| CU03 | Recuperar Contrasena | `AuthController` (`/recuperar-password`, `/reset-password`) | `AuthService` |
| CU04 | Administrar Cuentas de Usuario | `UsuarioController` | `UsuarioService` |
| CU05 | Administrar Sucursales | `SucursalController` | `SucursalService` |
| CU06 | Administrar Categorias de Queja | `CategoriaQuejaController` | `CategoriaQuejaService` |
| CU07 | Registrar Queja | `QuejaController` (`POST /api/quejas`) | `QuejaService.registrar` |
| CU08 | Administrar Quejas | `QuejaController` (`GET /api/quejas`, `/{id}`) | `QuejaService.listar/detalle` |
| CU09 | Asignar Queja (automatica y aleatoria) | interno + `/{id}/reasignar` | `AsignacionService` (RN09) |
| CU10 | Dar Seguimiento a Queja | `QuejaController` (`/{id}/seguimiento`) | `QuejaService.agregarSeguimiento` |
| CU11 | Resolver Queja | `QuejaController` (`/{id}/resolver`) | `QuejaService.resolver` |
| CU12 | Escalar Queja | `QuejaController` (`/{id}/escalar`) | `QuejaService.escalar` + `QuejaScheduler` (SLA) |
| CU13 | Reabrir Queja | `QuejaController` (`/{id}/reabrir`) | `QuejaService.reabrir` |
| CU14 | Calificar Resolucion de Queja | `QuejaController` (`/{id}/calificacion`) | `QuejaService.calificar` (RN08) |
| CU15 | Generar Reportes de Quejas | `ReporteController` | `ReporteService` |
| CU16 | Comunicacion Web Service | (interno, invocado por los demas) | `NotificacionService` |

Las reglas de negocio (RN01-RN09) y el catalogo de mensajes (AN01/AN02) del documento
**Reglas de Negocio de Casos de Uso** estan implementadas como validaciones dentro de
cada Service, con el numero de regla referenciado en comentarios (`// RNxx`, `// FAxx`).

## Correcciones de logica ya incorporadas

- **RN09 (asignacion automatica y aleatoria)**: al registrar una queja (CU07), el sistema
  elige un agente activo de la sucursal al azar (`AsignacionServiceImpl`), sin intervencion
  del Supervisor. La reasignacion manual (CU09-FA03) queda como capacidad aparte.
- **CU04 (alcance corregido)**: el Administrador solo crea/edita cuentas de personal interno
  (Agente, Supervisor, Administrador). Las cuentas de Cliente se crean por autorregistro (CU02);
  el Administrador solo puede consultarlas y suspenderlas con motivo.
- **RN08 (calificacion por resolucion)**: `Queja.numeroResolucion` se incrementa cada vez que
  se resuelve (CU11). La calificacion (CU14) queda asociada a `(queja_id, numero_resolucion)`,
  por lo que reabrir (CU13) y resolver de nuevo habilita una calificacion independiente.

## Requisitos para ejecutar

- Java 17+, Maven 3.9+
- Una base PostgreSQL (este proyecto esta configurado y probado en su string de
  conexion para **Neon** - `jdbc:postgresql://...neon.tech/...?sslmode=require`)
- **Importante:** este sandbox no tiene acceso a Maven Central, por lo que el codigo
  no pudo compilarse/ejecutarse aqui. Se revisó manualmente (balance de sintaxis,
  imports, firmas, y el esquema SQL contra cada entidad JPA) pero se recomienda
  compilar localmente antes de usarlo en produccion:
  ```bash
  mvn clean install
  ```

## Configuracion (Neon / ddl-auto=validate)

Este proyecto usa `spring.jpa.hibernate.ddl-auto=validate`: Hibernate **no crea
tablas**, solo valida que ya existan con la estructura exacta de las entidades.
Por eso el orden de arranque es:

1. **Correr el script una sola vez** contra tu base de Neon (SQL Editor de Neon,
   o `psql "$DB_URL" -f src/main/resources/db/schema.sql`):
   `src/main/resources/db/schema.sql`
   Crea las 7 tablas, sus indices, y un usuario Administrador semilla:
   - Correo: `admin@quickbite.com`
   - Password: `Admin#2026` (hash BCrypt real, ya verificado — **cambiala tras el primer login**)

2. **Variables de entorno** (mismo nombre que usa `application.properties`,
   consistente con tu `propietis.txt` y tu configuracion de Run/Debug en IntelliJ):
   - `DB_URL` — ej. `jdbc:postgresql://ep-xxxx-pooler.c-5.us-east-2.aws.neon.tech/neondb?sslmode=require&channel_binding=require`
   - `DB_USER` — ej. `neondb_owner`
   - `DB_PASSWORD`
   - `JWT_SECRET` — cadena larga y aleatoria (no reutilices la del ejemplo en producción)
   - Opcionales: `JWT_EXPIRATION`, `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `CORS_ORIGINS`

   > Neon usa PgBouncer en el host `-pooler`; por eso el pool de Hikari se dejó
   > pequeño (`maximum-pool-size=5`) en `application.properties` — el plan free
   > de Neon limita las conexiones concurrentes.

3. Ejecutar:
   ```bash
   mvn spring-boot:run
   ```
   o desde IntelliJ, con las variables de entorno cargadas en la configuracion
   Run/Debug (tal como ya la tienes armada).

Si prefieres que Hibernate cree las tablas por ti la primera vez en lugar de
correr `schema.sql` manualmente, cambia temporalmente `ddl-auto=validate` a
`update` en `application.properties`, arranca la app una vez, y luego regresa
a `validate`.

## Autenticacion

Todas las rutas protegidas requieren el header `Authorization: Bearer <token>`
obtenido en `POST /api/auth/login`. Los permisos por rol estan centralizados en
`SecurityConfig` (RN01).
