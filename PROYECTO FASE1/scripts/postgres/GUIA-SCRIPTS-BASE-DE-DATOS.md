# Guía: scripts de base de datos (PostgreSQL en contenedor)

Este documento describe **qué archivos SQL intervienen**, el **orden de ejecución** cuando usas Docker y **cómo cargar datos de prueba** después de tener todo levantado (o cómo automatizarlos en el primer arranque del volumen).

---

## 1. Ubicación de los scripts

| Ruta | Uso |
|------|-----|
| `scripts/postgres/docker-init/` | Se montan en el contenedor como `/docker-entrypoint-initdb.d` y PostgreSQL los ejecuta **solo la primera vez** que crea el volumen de datos. |
| `scripts/postgres/setup-completo-manual.sql` | Mismo esquema y usuarios, para instalar PostgreSQL **sin Docker** (ejecutar como superusuario con `psql`). |

---

## 2. Orden de ejecución en el contenedor (Docker)

El entrypoint de la imagen oficial ejecuta los archivos de `/docker-entrypoint-initdb.d` en **orden lexicográfico** (por nombre). Por eso los archivos están prefijados con `01`, `02`, `03`, `04`.

| Orden | Archivo | Base de datos activa | Qué hace |
|-------|---------|----------------------|----------|
| 1 | `01-create-taller-db.sql` | `logiflow_flota` (la definida por `POSTGRES_DB`) | Crea el rol `logiflow_taller` y la base `logiflow_taller`. |
| 2 | `02-schema-flota.sql` | `logiflow_flota` | Crea tablas `vehiculos` y `conductores` + índices y FK. |
| 3 | `03-schema-taller.sql` | Cambia a `logiflow_taller` (`\connect`) | Crea la tabla `ordenes_mantenimiento` y asigna propietario al rol del taller. |
| 4 | `04-datos-demo-flota.sql` | `logiflow_flota` (nueva sesión `psql` vuelve a la BD por defecto) | Inserta **vehículos y conductores de demostración** (idempotente). |

**Importante:** esa secuencia corre **una sola vez** al inicializar el volumen. Si ya tenías datos y quieres repetir el init:

```powershell
docker compose down -v
docker compose up -d
```

(`-v` elimina el volumen; en producción no lo uses sin copia de seguridad.)

---

## 3. Orden lógico “a mano” (sin depender del nombre del fichero)

Si documentas o ejecutas tú mismo los pasos, el orden correcto es:

1. **Usuarios y bases** (superusuario): crear `logiflow_flota` y `logiflow_taller` con sus roles (equivalente a `01` + variables del contenedor para la primera BD).
2. **Esquema Flota** en `logiflow_flota`: tablas de vehículos y conductores (`02`).
3. **Esquema Taller** en `logiflow_taller`: tabla de órdenes (`03`).
4. **Datos opcionales** de negocio / demo en cada base que los necesite (`04` solo afecta a Flota; Taller puede quedar vacío al inicio).

---

## 4. Cómo crear datos **después** de ejecutar todo

Tienes tres formas coherentes con este proyecto; elige una.

### 4.1 Ya ejecutaste Docker y los cuatro scripts del `docker-init`

Si el volumen se creó con los archivos `01`–`04` presentes, **los datos demo de Flota ya están cargados**. No necesitas hacer nada más para probar la API de Flota.

Arranca solo los microservicios:

```powershell
cd ms-flota-rest
mvn spring-boot:run
```

El componente `DemoDataLoader` de Spring **comprueba si ya hay vehículos**; si la tabla no está vacía, **no inserta nada** (no duplica).

### 4.2 Contenedor arriba pero **sin** el archivo `04` (solo esquema)

Generaste el volumen antes de añadir `04`, o quitaste ese archivo del directorio:

1. Asegúrate de que PostgreSQL escucha (por ejemplo `docker compose up -d`).
2. Carga el SQL de demo contra **Flota**:

```powershell
# Windows PowerShell — ajusta la ruta al repo
$env:PGPASSWORD = "flota_secret"
psql -h localhost -p 5433 -U logiflow_flota -d logiflow_flota -f scripts/postgres/docker-init/04-datos-demo-flota.sql
```

En Linux/macOS:

```bash
PGPASSWORD=flota_secret psql -h localhost -p 5433 -U logiflow_flota -d logiflow_flota -f scripts/postgres/docker-init/04-datos-demo-flota.sql
```

### 4.3 Sin SQL manual: solo arrancar **ms-flota-rest**

Si las tablas existen pero están **vacías**, al iniciar la aplicación el `DemoDataLoader` inserta los mismos registros que el script `04` (misma semántica de negocio).

Orden recomendado:

1. `docker compose up -d` (o script manual `setup-completo-manual.sql`).
2. `mvn spring-boot:run` en `ms-flota-rest`.
3. Luego `mvn spring-boot:run` en `ms-taller-rest` si necesitas el ACL.

### 4.4 Datos en el contexto **Taller** (`logiflow_taller`)

No hay semilla obligatoria: las órdenes aparecen cuando llamas al API, por ejemplo:

```http
POST http://localhost:8082/api/taller/v1/ordenes-mantenimiento
Content-Type: application/json

{"matricula":"XYZ5678","descripcion":"Revisión preventiva"}
```

Si quisieras un `INSERT` manual de prueba en `ordenes_mantenimiento`, hazlo **conectado a la base `logiflow_taller`** y respeta los nombres de columnas del script `03-schema-taller.sql`.

---

## 5. Equivalencia con instalación manual (sin contenedor)

Ejecuta **una vez** como superusuario:

```powershell
psql -U postgres -h localhost -f scripts/postgres/setup-completo-manual.sql
```

Ese archivo incluye usuarios, bases y esquemas (equivalente a `01`+`02`+`03`).  
Después, si quieres los mismos datos demo en Flota, ejecuta **`04-datos-demo-flota.sql`** contra `logiflow_flota` como en el apartado 4.2.

---

## 6. Resumen visual

```text
Docker primer arranque (volumen nuevo)
    │
    ├─► 01-create-taller-db.sql     (usuarios/BD taller)
    ├─► 02-schema-flota.sql          (tablas Flota)
    ├─► 03-schema-taller.sql         (tablas Taller)
    └─► 04-datos-demo-flota.sql      (datos demo opcionales en Flota)

Después (cada vez que desarrollas)
    │
    ├─► ms-flota-rest  (8081)  — DemoDataLoader solo si tablas vacías
    └─► ms-taller-rest (8082)  — datos vía API o INSERT manual
```

Con esto queda documentado **qué scripts van al contenedor**, **en qué orden** y **cómo y cuándo poblar datos** tras tener el entorno ejecutado.
