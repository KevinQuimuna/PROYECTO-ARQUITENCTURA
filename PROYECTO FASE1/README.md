# LogiFlow — Fase 1 (monorepo Maven)

Microservicios Spring Boot 3.2 / Java 17:

| Módulo | Puerto | Base de datos |
|--------|--------|----------------|
| `ms-flota-rest` | 8081 | PostgreSQL `logiflow_flota` |
| `ms-taller-rest` | 8082 | PostgreSQL `logiflow_taller` |

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

- Swagger Flota: http://localhost:8081/swagger-ui.html  
- Swagger Taller: http://localhost:8082/swagger-ui.html  

La documentación completa (DDD, DevOps, Telegram, contratos) está en **[GUÍA-FASE1-LOGIFLOW.md](GUÍA-FASE1-LOGIFLOW.md)**.

## Ramas Git sugeridas

- `main` — estable / producción  
- `development` — integración continua  

El workflow `.github/workflows/ci.yml` se dispara en `push` y `pull_request` sobre esas ramas.
