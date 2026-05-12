# Solución: error de conexión / contraseña con PostgreSQL (ms-flota-rest)

## Qué significa tu error

El mensaje clave es:

`FATAL: la autentificación password falló para el usuario «logiflow_flota»`

Eso indica que **sí llegaste a un servidor PostgreSQL**, pero **usuario y contraseña no coinciden** con lo que ese servidor espera, o estás conectando a **otro PostgreSQL** distinto del contenedor LogiFlow.

El error secundario de Hibernate (`Unable to determine Dialect...`) es **consecuencia** de no poder abrir la conexión JDBC; no es el problema raíz.

---

## Causa más frecuente en Windows

Tienes **PostgreSQL instalado en el PC** escuchando en el puerto **5432**. La aplicación intentaba `localhost:5432` y se conectaba a **ese** servidor, no al de Docker. Ahí el usuario `logiflow_flota` con contraseña `flota_secret` **no existe o tiene otra clave** → fallo de autenticación.

**Cambio aplicado en el proyecto:** Docker publica el puerto **5433** en tu máquina (`5433:5432`) y las URLs por defecto de Spring usan **localhost:5433**. Así la app y el contenedor no compiten con el PostgreSQL nativo en 5432.

Pasos:

1. En la **raíz del repo** (donde está `docker-compose.yml`):

   ```powershell
   docker compose down -v
   docker compose up -d
   ```

   El `-v` solo si quieres **reiniciar el volumen** (borra datos del contenedor). Si ya tienes datos que quieres conservar, usa `docker compose down` sin `-v`.

2. Comprueba que el contenedor está arriba:

   ```powershell
   docker ps
   ```

3. Prueba login con **puerto 5433**:

   ```powershell
   $env:PGPASSWORD = "flota_secret"
   psql -h localhost -p 5433 -U logiflow_flota -d logiflow_flota -c "SELECT 1"
   ```

4. Arranca de nuevo:

   ```powershell
   cd ms-flota-rest
   mvn spring-boot:run
   ```

---

## Si sigue fallando

### A) Volumen de Docker creado con otras variables

Si el volumen se creó cuando `POSTGRES_PASSWORD` era distinto, la contraseña quedó “vieja”. Solución: `docker compose down -v` y `docker compose up -d` (recrea datos desde cero).

### B) Quieres usar tu PostgreSQL local en 5432

No uses el puerto por defecto del proyecto; define variables al arrancar (PowerShell):

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/logiflow_flota"
$env:SPRING_DATASOURCE_USERNAME = "tu_usuario"
$env:SPRING_DATASOURCE_PASSWORD = "tu_clave"
mvn spring-boot:run
```

Asegúrate de haber creado la base y el usuario con el script `scripts/postgres/setup-completo-manual.sql` (o equivalente) en **ese** servidor.

### C) El contenedor no está en ejecución

Si no hay nada escuchando en 5433, verás error de conexión rechazada (no siempre “password failed”). Arranca Docker Desktop y `docker compose up -d`.

---

## Resumen de credenciales por defecto (Docker actual)

| Aplicación | JDBC (host) | Usuario | Contraseña |
|------------|-------------|---------|------------|
| ms-flota-rest | `jdbc:postgresql://localhost:5433/logiflow_flota` | `logiflow_flota` | `flota_secret` |
| ms-taller-rest | `jdbc:postgresql://localhost:5433/logiflow_taller` | `logiflow_taller` | `taller_secret` |

Puedes sobreescribir todo con `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD`.
