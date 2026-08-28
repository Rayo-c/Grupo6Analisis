# Sistema de Gestion de Quejas - QuickBite

Implementacion del sistema a partir de los 17 documentos de casos de uso
(CU00 Portal a CU16 Comunicacion Web Service) y las Reglas de Negocio (RN01-RN09).

- **`backend/`** - Spring Boot 3 / Java 17 / PostgreSQL / JWT. Arquitectura por capas
  (model, repository/DAO, service, controller, security, dto).
- **`frontend/`** - Angular 17 (standalone components) / TypeScript. Arquitectura por
  capas (core/services, core/guards, core/interceptors, features, shared).

Cada carpeta tiene su propio `README.md` con el detalle de arquitectura, mapeo de
casos de uso y pasos para ejecutar.

## Mapeo general Caso de Uso -> Backend -> Frontend

| CU | Nombre | Backend | Frontend |
|----|---|---|---|
| CU00 | Portal | `PortalController` | `features/portal` |
| CU01 | Iniciar Sesion | `AuthController` / `AuthService` | `features/auth/login` |
| CU02 | Registrar Cliente | `AuthController` / `AuthService` | `features/auth/registro` |
| CU03 | Recuperar Contrasena | `AuthController` / `AuthService` | `features/auth/recuperar-password` |
| CU04 | Administrar Cuentas de Usuario | `UsuarioController` / `UsuarioService` | `features/admin/cuentas-personal`, `cuentas-clientes` |
| CU05 | Administrar Sucursales | `SucursalController` / `SucursalService` | `features/admin/sucursales` |
| CU06 | Administrar Categorias de Queja | `CategoriaQuejaController` / `CategoriaQuejaService` | `features/admin/categorias` |
| CU07 | Registrar Queja | `QuejaController` / `QuejaService.registrar` | `features/cliente/registrar-queja` |
| CU08 | Administrar Quejas | `QuejaController` (listar/detalle) | `mis-quejas`, `bandeja-quejas`, `detalle-queja` |
| CU09 | Asignar Queja (automatica y aleatoria) | `AsignacionService` (RN09) | interno + boton "Reasignar" disponible en el servicio |
| CU10 | Dar Seguimiento a Queja | `QuejaService.agregarSeguimiento` | `detalle-queja` |
| CU11 | Resolver Queja | `QuejaService.resolver` + `QuejaScheduler` | `detalle-queja` |
| CU12 | Escalar Queja | `QuejaService.escalar` + `QuejaScheduler` (SLA) | `detalle-queja` |
| CU13 | Reabrir Queja | `QuejaService.reabrir` | `detalle-queja` |
| CU14 | Calificar Resolucion de Queja | `QuejaService.calificar` (RN08) | `detalle-queja` (`app-star-rating`) |
| CU15 | Generar Reportes de Quejas | `ReporteController` / `ReporteService` | `features/reportes` |
| CU16 | Comunicacion Web Service | `NotificacionService` | (interno, sin pantalla) |

## Correcciones de logica de negocio incorporadas

Estas correcciones surgieron de la revision de los documentos de casos de uso y ya
estan reflejadas tanto en el codigo como en los `.docx`:

1. **RN09 - Asignacion automatica y aleatoria**: al registrar una queja, el sistema
   asigna un agente activo de la sucursal al azar, sin intervencion del Supervisor.
   La reasignacion manual queda como capacidad aparte (CU09-FA03).
2. **CU04 - Alcance de "Administrar Cuentas de Usuario"**: el Administrador solo
   crea/edita cuentas de personal interno (Agente, Supervisor, Administrador). Las
   cuentas de Cliente se crean por autorregistro (CU02); el Administrador solo puede
   consultarlas y suspenderlas con motivo.
3. **RN08 - Calificacion por resolucion**: cada resolucion de una queja (contador
   `numeroResolucion`) admite su propia calificacion independiente. Si la queja se
   reabre y se resuelve de nuevo, se habilita una nueva calificacion sin perder la
   anterior en el historial.

## Limitaciones importantes de este entorno de generacion

Este codigo se escribio directamente (sin scaffolding automatico), porque el entorno
en el que se genero **no tiene acceso a Maven Central ni puede ejecutar `npm install`
completo / Angular CLI**. Esto significa:

- El backend **no se compilo** con `mvn` en este entorno; se validó manualmente
  (balance de sintaxis, firmas, imports) pero **debes correr `mvn clean install`
  localmente antes de usarlo**, y corregir cualquier detalle de compilacion que surja.
- El frontend **no se genero con `ng new` ni se ejecutó `npm install`**; se siguió al
  detalle la estructura estandar de Angular 17 standalone y se verificó que todas las
  rutas y `templateUrl` referencien archivos existentes, pero **debes correr
  `npm install` localmente** antes de levantar el servidor de desarrollo.
- No se incluyen pruebas automatizadas (unitarias/integracion) ni pipeline CI/CD.
- La plantilla de correo (CU16) usa `JavaMailSender` con SMTP generico; deberas
  configurar un proveedor real (SendGrid, SES, etc.) en produccion.

En otras palabras: es un **esqueleto funcional completo y coherente con los 16 casos
de uso**, listo para compilar, ajustar y extender - no un sistema ya probado en
ejecucion.

## Orden sugerido para ponerlo en marcha

1. `cd backend && mvn clean install` (corrige lo que la compilacion senale)
2. Crear la base de datos PostgreSQL y variables de entorno (ver `backend/README.md`)
3. `mvn spring-boot:run`
4. `cd frontend && npm install`
5. Ajustar `src/environments/environment.ts` si el backend no corre en `localhost:8080`
6. `npm start` y abrir `http://localhost:4200`
