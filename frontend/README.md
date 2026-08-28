# Frontend - Sistema de Gestion de Quejas (QuickBite)

Angular 17 (standalone components) / TypeScript. Consume la API del backend Spring Boot.

## Arquitectura

```
src/app/
├── core/
│   ├── models/          Interfaces TS equivalentes a los DTOs del backend
│   ├── services/        Un servicio HTTP por dominio (auth, queja, usuario, sucursal, categoria, reporte, portal)
│   ├── interceptors/     jwt.interceptor (agrega el token), error.interceptor (traduce errores de la API)
│   └── guards/           authGuard (requiere sesion), roleGuard (requiere rol - RN01)
├── features/
│   ├── portal/            CU00 - Pagina de inicio (mision, vision, valores)
│   ├── auth/
│   │   ├── login/          CU01
│   │   ├── registro/       CU02
│   │   └── recuperar-password/  CU03
│   ├── cliente/
│   │   ├── registrar-queja/    CU07 (autenticado o invitado)
│   │   ├── mis-quejas/         CU08 (vista cliente)
│   │   └── detalle-queja/      CU08 detalle + CU10/CU11/CU12/CU13/CU14 (segun rol y estado)
│   ├── agente-supervisor/
│   │   └── bandeja-quejas/     CU08 (vista agente/supervisor, con filtros)
│   ├── admin/
│   │   ├── cuentas-personal/   CU04 (Agente/Supervisor/Administrador)
│   │   ├── cuentas-clientes/   CU04 (solo consulta + suspension con motivo)
│   │   ├── sucursales/         CU05
│   │   └── categorias/         CU06
│   └── reportes/                CU15
├── shared/components/
│   ├── navbar/            Menu dinamico segun el rol autenticado
│   └── star-rating/       Selector de estrellas usado en CU14
├── app.routes.ts           Enrutamiento (lazy-loaded standalone components)
└── app.config.ts           Providers (router, HttpClient + interceptores)
```

CU09 (asignacion automatica y aleatoria) y CU16 (comunicacion / envio de notificaciones)
son procesos internos del backend sin pantalla propia; CU09-FA03 (reasignacion manual) esta
disponible desde el servicio `QuejaService.reasignar()`, listo para conectarse a un boton en
`bandeja-quejas` si se desea exponerlo en la UI.

## Correcciones de logica reflejadas en el frontend

- **CU04** separado en dos pantallas: "Cuentas de Personal" (crear/editar Agente, Supervisor,
  Administrador) y "Cuentas de Cliente" (solo consulta y suspension con motivo obligatorio).
- **CU14** el boton "Calificar Atencion" solo aparece si la queja esta Resuelta y no existe ya
  una calificacion para el `numeroResolucion` actual (`puedeCalificar` en `detalle-queja`),
  reflejando RN08.
- **CU13** el boton "Reabrir Queja" solo aparece si `vecesReabierta === 0`, consistente con la
  regla de una sola reapertura.

## Requisitos para ejecutar

- Node.js 18+ y npm
- **Importante:** este sandbox no tiene acceso a un entorno con `npm install` completo
  para Angular verificado end-to-end (se genero el codigo a mano siguiendo la estructura
  estandar de Angular 17 standalone). Se revisaron manualmente balance de sintaxis e
  integridad de referencias (rutas, `templateUrl`), pero se recomienda instalar y compilar
  localmente antes de usarlo en produccion.

```bash
npm install
npm start          # http://localhost:4200
```

Configura la URL del backend en `src/environments/environment.ts` (por defecto
`http://localhost:8080/api`).
