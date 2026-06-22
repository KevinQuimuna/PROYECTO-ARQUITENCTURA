# LogiFlow — Fase 2 (monorepo Maven)

Microservicios Spring Boot 3.2 / Java 17:

| Módulo | Puerto | Base de datos | Descripción |
|--------|--------|----------------|-------------|
| `ms-flota-rest` | 8012 | PostgreSQL `logiflow_flota` | Gestión de flota de vehículos |
| `ms-taller-rest` | 8082 | PostgreSQL `logiflow_taller` | Gestión de taller de mantenimiento |
| `ms-clientes` | 8084 | PostgreSQL `logiflow_clientes` | Gestión de clientes y cuentas corporativas |
| `ms-pedidos` | 8085 | PostgreSQL `logiflow_pedidos` | Gestión de pedidos |
| `ms-ruteo` | 8086 | PostgreSQL `logiflow_ruteo` | Gestión de rutas y envíos |
| `ms-seguimiento` | 8087 | PostgreSQL `logiflow_seguimiento` | Seguimiento de envíos en tiempo real |
| `ms-auth` | 8088 | PostgreSQL `logiflow_auth` | Autenticación y autorización |
| `graphql-gateway` | 8089 | - | Gateway GraphQL unificado |
| `logiflow-common` | - | - | Librería compartida (eventos, DTOs) |

## PostgreSQL

**Con Docker (recomendado):** en la raíz del repo. El contenedor expone PostgreSQL en el host en el puerto **5433** (para no chocar con un PostgreSQL nativo de Windows en 5432).

```powershell
docker compose up -d
```

**Sin Docker:** ejecuta como superusuario `scripts/postgres/setup-completo-manual.sql` (ver comentarios al inicio del archivo).

Cada servicio usa **su propia base** en el mismo servidor (alineado con microservicios y DDD). Detalle y alternativas en [GUÍA-FASE1-LOGIFLOW.md](GUÍA-FASE1-LOGIFLOW.md) §1.4.

**Orden de scripts SQL en el contenedor y datos demo:** [scripts/postgres/GUIA-SCRIPTS-BASE-DE-DATOS.md](scripts/postgres/GUIA-SCRIPTS-BASE-DE-DATOS.md).

**Error «password authentication failed» al arrancar:** [docs/SOLUCION-ERROR-POSTGRES.md](docs/SOLUCION-ERROR-POSTGRES.md).

## Arranque rápido

```powershell
cd "ruta\al\proyecto"
mvn -q verify
```

Con PostgreSQL ya creado (Docker o script):

Terminal 1 — Flota:

```powershell
cd ms-flota-rest
mvn spring-boot:run
```

Terminal 2 — Taller (requiere Flota arriba):

```powershell
cd ms-taller-rest
mvn spring-boot:run
```

Terminal 3 — Clientes:

```powershell
cd ms-clientes
mvn spring-boot:run
```

Terminal 4 — Pedidos:

```powershell
cd ms-pedidos
mvn spring-boot:run
```

Terminal 5 — Ruteo:

```powershell
cd ms-ruteo
mvn spring-boot:run
```

Terminal 6 — Seguimiento:

```powershell
cd ms-seguimiento
mvn spring-boot:run
```

Terminal 7 — Auth:

```powershell
cd ms-auth
mvn spring-boot:run
```

Terminal 8 — GraphQL Gateway:

```powershell
cd graphql-gateway
mvn spring-boot:run
```

- Swagger Flota: http://localhost:8012/swagger-ui.html
- Swagger Taller: http://localhost:8082/swagger-ui.html
- Swagger Clientes: http://localhost:8084/swagger-ui.html
- Swagger Pedidos: http://localhost:8085/swagger-ui.html
- Swagger Ruteo: http://localhost:8086/swagger-ui.html
- Swagger Seguimiento: http://localhost:8087/swagger-ui.html
- Swagger Auth: http://localhost:8088/swagger-ui.html
- GraphQL Playground: http://localhost:8089/graphql  

La documentación completa (DDD, DevOps, Telegram, contratos) está en **[GUÍA-FASE1-LOGIFLOW.md](GUÍA-FASE1-LOGIFLOW.md)**.

## Ramas Git sugeridas

- `main` — estable / producción
- `development` — integración continua

El workflow `.github/workflows/ci.yml` se dispara en `push` y `pull_request` sobre esas ramas.

## Arquitectura de Componentes

### Diagrama de Microservicios

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  GraphQL Gateway│────│   ms-clientes   │────│  PostgreSQL      │
│     (8089)      │    │     (8084)      │    │  (logiflow_     │
└─────────────────┘    └─────────────────┘    │   clientes)      │
                       ┌─────────────────┐    └─────────────────┘
                       │   ms-pedidos    │────│  PostgreSQL      │
                       │     (8085)      │    │  (logiflow_     │
                       └─────────────────┘    │   pedidos)       │
                       ┌─────────────────┐    └─────────────────┘
                       │   ms-ruteo      │────│  PostgreSQL      │
                       │     (8086)      │    │  (logiflow_     │
                       └─────────────────┘    │   ruteo)        │
                       ┌─────────────────┐    └─────────────────┘
                       │ ms-seguimiento  │────│  PostgreSQL      │
                       │     (8087)      │    │  (logiflow_     │
                       └─────────────────┘    │   seguimiento)  │
                       ┌─────────────────┐    └─────────────────┘
                       │    ms-auth       │────│  PostgreSQL      │
                       │     (8088)      │    │  (logiflow_     │
                       └─────────────────┘    │   auth)         │
                       ┌─────────────────┐    └─────────────────┘
                       │  ms-flota-rest  │────│  PostgreSQL      │
                       │     (8012)      │    │  (logiflow_     │
                       └─────────────────┘    │   flota)        │
                       ┌─────────────────┐    └─────────────────┘
                       │ ms-taller-rest  │────│  PostgreSQL      │
                       │     (8082)      │    │  (logiflow_     │
                       └─────────────────┘    │   taller)       │
                                               └─────────────────┘

                    ┌─────────────────┐
                    │   RabbitMQ      │
                    │  (localhost:    │
                    │   5672/15672)   │
                    └─────────────────┘
```

### Flujos de Eventos (RabbitMQ)

1. **Pedido Creado** (ms-pedidos → ms-ruteo)
   - Evento: `PedidoCreadoEvent`
   - Exchange: `pedidos.exchange`
   - Queue: `ruteo.pedidos.queue`
   - Acción: ms-ruteo crea automáticamente un envío y asigna vehículo

2. **Envío Actualizado** (ms-ruteo → ms-seguimiento)
   - Evento: `EnvioActualizadoEvent`
   - Exchange: `ruteo.exchange`
   - Queue: `seguimiento.envios.queue`
   - Acción: ms-seguimiento actualiza ubicación en tiempo real

3. **Envío Entregado** (ms-ruteo → ms-pedidos)
   - Evento: `EnvioEntregadoEvent`
   - Exchange: `ruteo.exchange`
   - Queue: `pedidos.envios.queue`
   - Acción: ms-pedidos actualiza estado del pedido a ENTREGADO

### DevOps

- **CI/CD**: GitHub Actions en `.github/workflows/ci.yml`
- **Análisis Estático**: SonarCloud (configurado en `sonar-project.properties`)
- **Notificaciones**: Telegram (configurado en secrets del repositorio)
- **Cobertura**: JaCoCo para todos los microservicios

### Configuración de Secrets en GitHub

Para que el pipeline funcione correctamente, configura los siguientes secrets en tu repositorio:

- `SONAR_TOKEN`: Token de SonarCloud
- `SONAR_ORGANIZATION`: Organización en SonarCloud
- `SONAR_PROJECT_KEY`: Clave del proyecto en SonarCloud
- `TELEGRAM_BOT_TOKEN`: Token del bot de Telegram
- `TELEGRAM_CHAT_ID`: ID del chat de Telegram para notificaciones
