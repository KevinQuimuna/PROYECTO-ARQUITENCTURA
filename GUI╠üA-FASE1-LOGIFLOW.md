# Guía LogiFlow — Fase 1: DDD, pilotos REST y DevOps

Este documento resume **qué se implementó**, **cómo funciona** la solución en Spring Boot y **cómo configurar** SonarCloud y Telegram para cumplir el entregable de la asignatura.

---

## 1. Qué se entregó en código

### 1.1 Monorepo Maven (`logiflow-fase1`)

- **Parent POM** (`pom.xml`): agrupa dos módulos ejecutables.
- **`ms-flota-rest`**: API REST con OpenAPI/Swagger, persistencia **PostgreSQL**, CRUD de **vehículos** y **conductores**, endpoints de **disponibilidad** pensados para el contexto de Ruteo.
- **`ms-taller-rest`**: **Anticorruption Layer (ACL)** REST que:
  - expone el contrato “hacia el taller externo”;
  - consulta datos internos llamando a **ms-flota-rest** (`RestClient`);
  - persiste órdenes de mantenimiento en **PostgreSQL** (base propia);
  - al registrar una orden, actualiza el vehículo en Flota a estado **MANTENIMIENTO** (coherencia entre contextos en el piloto).

### 1.2 Infraestructura CI/CD

- **`.github/workflows/ci.yml`**: en cada `push` / `pull_request` a `main` o `development`:
  1. Compila y ejecuta tests: `mvn verify` (incluye JaCoCo).
  2. Opcionalmente ejecuta **SonarCloud** si están configurados los secretos.
  3. Envía un resumen a **Telegram** si están `TELEGRAM_BOT_TOKEN` y `TELEGRAM_CHAT_ID` (el job corre `always()` para informar éxito o fallo del build Maven).

### 1.3 Contrato WSDL vs REST

El enunciado mezcla **REST** con **WSDL**. En la práctica industrial:

- **WSDL** describe servicios tipo **SOAP** (XML sobre HTTP con envelope SOAP).
- Los microservicios **REST** se contratan con **OpenAPI** (Swagger).

En este proyecto:

- La **fuente de verdad del contrato** es **OpenAPI** (`/swagger-ui.html` y `/api-docs` en cada servicio).
- Se añade el archivo **`ms-taller-rest/docs/contrato-taller.wsdl`** como **anexo de tipos de datos (XSD embebido)** alineado con las respuestas JSON del ACL. Sirve para documentación y para cursos que piden “un WSDL”; no implica endpoint SOAP en ejecución.

### 1.4 Persistencia: PostgreSQL y decisión “una BD por servicio”

**Implementación:** ambos microservicios usan **PostgreSQL** con `spring.jpa.hibernate.ddl-auto: validate` (el esquema debe existir; no se deja que Hibernate cree tablas en arranque de producción). Los scripts SQL del repositorio crean usuarios, bases y tablas.

**¿Una base de datos por microservicio o una sola base compartida?**

| Enfoque | Ventajas | Inconvenientes |
|---------|----------|----------------|
| **Una base lógica por microservicio** (recomendado en DDD / microservicios) | Límites de contexto claros, despliegue y escalado independientes, sin acoplar esquemas entre equipos. | Más usuarios/BD que administrar (aunque pueden vivir en el **mismo servidor** PostgreSQL). |
| **Una sola base compartida** | Menos objetos que crear en el motor. | Acopla datos y migraciones; un cambio de esquema puede romper otro servicio; contradice el principio de “una base por servicio”. |

**Recomendación para LogiFlow:** **una base de datos (o esquema con usuario dedicado) por bounded context / microservicio**. En este proyecto: `logiflow_flota` para **ms-flota-rest** y `logiflow_taller` para **ms-taller-rest**, en el mismo contenedor o servidor PostgreSQL. Así se alinea con el enunciado (“cada microservicio posee su propia base de datos”) sin necesidad de dos máquinas físicas.

**Tests automatizados:** `ms-flota-rest` usa **H2 solo en el perfil `test`** (dependencia `test`) para que `mvn verify` no exija PostgreSQL en CI; en runtime los JAR usan el driver PostgreSQL.

**Scripts y Docker:**

- **`docker-compose.yml`** (raíz): levanta PostgreSQL 16 y monta `scripts/postgres/docker-init/` para crear la segunda BD, las tablas y (opcional) datos demo la **primera vez** que arranca el volumen. El orden y el detalle de cada `.sql` están en **`scripts/postgres/GUIA-SCRIPTS-BASE-DE-DATOS.md`**.
- **`scripts/postgres/setup-completo-manual.sql`**: mismo contenido lógico para ejecutar con `psql` como superusuario en una instalación ya existente.
- Si cambias el volumen de Docker y necesitas recrear esquemas: `docker compose down -v` y vuelve a subir (borra datos).

**Variables de entorno (opcional):** `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` sobreescriben los valores por defecto de cada `application.yml`.

---

## 2. Cómo funciona la solución (visión técnica)

### 2.1 ms-flota-rest (puerto 8081)

**Responsabilidad:** administración de la flota según el lenguaje ubicuo de LogiFlow.

| Recurso | Ruta base | Operaciones |
|---------|-----------|-------------|
| Vehículos | `/api/v1/vehiculos` | GET lista, GET `/{id}`, GET `/matricula/{matricula}`, POST, PUT, DELETE |
| Disponibilidad (Ruteo) | `/api/v1/vehiculos/disponibles` | GET; query opcional `tipo` = `MOTO`, `AUTO`, `FURGONETA`, `CAMION` |
| Conductores | `/api/v1/conductores` | CRUD estándar |
| Disponibilidad conductores | `/api/v1/ruteo/conductores-disponibles` | GET — conductores en estado `DISPONIBLE` |

**Modelo de datos (dominio Flota):**

- **Vehículo:** `matricula`, `tipo`, `capacidadKg`, `autonomiaKm`, `estado` (`DISPONIBLE`, `EN_SERVICIO`, `MANTENIMIENTO`).
- **Conductor:** `nombreCompleto`, `licencia`, `vehiculoAsignado` (opcional), `estado` (`DISPONIBLE`, `EN_SERVICIO`, `NO_DISPONIBLE`).

Al arrancar en perfil **default** se cargan **datos de demostración** (tres vehículos y dos conductores). En tests el perfil `test` desactiva esa carga (`DemoDataLoader` con `@Profile("!test")`).

### 2.2 ms-taller-rest (puerto 8082)

**Responsabilidad:** traducir entre el modelo de **Flota** y el **modelo que espera el taller externo** (ACL).

| Operación lógica (enunciado) | HTTP | Ruta |
|------------------------------|------|------|
| `consultarVehiculo(matricula)` | GET | `/api/taller/v1/vehiculos/{matricula}` |
| `registrarOrdenMantenimiento(matricula, descripcion)` | POST | `/api/taller/v1/ordenes-mantenimiento` (JSON body) |

**Respuesta de consulta (vista taller):** `codigoRespuesta` (`OK` / `NOT_FOUND`) y objeto `vehiculo` con campos como `tipoEquipo` (`EQ-MOTO`, …) y `estadoOperativoTaller` (`OPERATIVO`, `EN_SERVICIO`, `EN_TALLER`), mapeados desde el JSON de Flota en `TallerAnticorruptionMapper`.

**Configuración:** `logiflow.flota.base-url` en `application.yml` (por defecto `http://localhost:8081`). Si Flota no está levantada, el cliente REST lanza `ResourceAccessException` y el `TallerExceptionHandler` responde **502** con mensaje claro.

### 2.3 Flujo ejemplo (orden de mantenimiento)

1. Cliente POST a Taller con `matricula` + `descripcion`.
2. Taller persiste `OrdenMantenimiento`.
3. Taller llama a Flota GET por matrícula; si existe, PUT actualizando `estado` a `MANTENIMIENTO`.

---

## 3. Análisis DDD (propuesta para el documento de arquitectura)

### 3.1 Dominio principal (Core Domain)

**Gestión de la cadena de entrega:** asignación de pedidos a vehículos, optimización de rutas y seguimiento en tiempo real. En Fase 1 **no** se implementa aún; aquí solo se **preparan** datos de flota y contratos que Ruteo y Seguimiento consumirán en fases posteriores.

### 3.2 Subdominios

| Tipo | Subdominios |
|------|-------------|
| **Soporte** | Clientes, Flota, Tarifas/Facturación, Notificaciones |
| **Genéricos** | Autenticación/Autorización, Integración taller (REST) |

### 3.3 Bounded contexts (tabla resumida)

| Contexto | Responsabilidad | Términos ubicuo (ejemplos) |
|----------|-----------------|----------------------------|
| **Pedidos** | Ciclo de vida del pedido | Pedido, Origen, Destino, Paquete, Estado, Prioridad |
| **Flota** | Vehículos y conductores | Vehículo, Conductor, Tipo, Capacidad, Autonomía, Estado |
| **Taller** | ACL taller mecánico | OrdenMantenimiento, Matrícula, Descripción, Fecha |
| **Ruteo** | Asignación y rutas | Envío, Ruta, Parada, ETA, km |
| **Seguimiento** | Tiempo real (futuro WebSocket) | Posición, Velocidad, ETA, Evento |
| **Facturación** | Costos e invoices | Tarifa, Trayecto, Peso, Recargo, Factura |
| **Notificaciones** | Canales salientes | Evento, Destinatario, Canal |
| **Clientes** | Maestro de clientes | Cliente, Cuenta, Contrato, Saldo |
| **Autenticación** | Identidad | Usuario, Rol, Token |
| **GraphQL Gateway** | BFF | Query, Mutation, Resolver |

### 3.4 Event Storming (resumen para el informe)

**Eventos de dominio (ejemplos):** `PedidoCreado`, `PedidoAsignado`, `EnvioEnRuta`, `EnvioEntregado`, `PosicionActualizada`, `FacturaEmitida`, `OrdenMantenimientoRegistrada`, `VehiculoPuestoEnMantenimiento`.

**Comandos:** `CrearPedido`, `AsignarVehiculo`, `RegistrarEntrega`, `RegistrarOrdenMantenimiento`, `ActualizarPosicion`.

**Agregados candidatos:** `Pedido` (Pedidos), `Vehiculo`/`Conductor` (Flota), `OrdenMantenimiento` (Taller ACL), `Factura` (Facturación).

### 3.5 Context Map (patrones del enunciado)

| Relación | Contextos | Notas |
|----------|-----------|--------|
| **Partnership** | Flota ↔ Ruteo | Coordinación estrecha en asignación; en piloto, consultas REST de disponibilidad. |
| **Customer / Supplier** | Ruteo (customer) ← Pedidos, Flota (suppliers); Ruteo → Seguimiento | Ruteo consume APIs; expone datos de envío al seguimiento. |
| **Conformist** | Facturación → eventos de Pedidos | Facturación se adapta al modelo publicado por Pedidos. |
| **Anticorruption Layer** | **Taller** | Este repositorio: `ms-taller-rest` traduce Flota ↔ contrato taller. |
| **Open Host Service** | Flota (REST) | API estable para consumidores internos (Ruteo, Taller ACL). |
| **Customer** | GraphQL Gateway | Cliente de múltiples servicios (fase posterior). |

---

## 4. Ejecución local detallada

### Requisitos

- JDK **17**
- Maven **3.9+**
- **Docker Desktop** (recomendado) o PostgreSQL 14+ instalado localmente

### 4.1 Base de datos PostgreSQL

**Opción A — Docker (recomendada)**

En la raíz del proyecto:

```powershell
docker compose up -d
```

Esto crea el usuario `logiflow_flota`, la BD `logiflow_flota`, el usuario `logiflow_taller`, la BD `logiflow_taller` y las tablas (scripts en `scripts/postgres/docker-init/`). Credenciales por defecto alineadas con `application.yml`:

- Flota: `jdbc:postgresql://localhost:5433/logiflow_flota` / usuario `logiflow_flota` / contraseña `flota_secret`
- Taller: `jdbc:postgresql://localhost:5433/logiflow_taller` / usuario `logiflow_taller` / contraseña `taller_secret`

(El contenedor Docker expone **5433** en el host para no chocar con un PostgreSQL de Windows que suele usar **5432**.)

Si al arrancar Spring ves **«password authentication failed»**, lee **[docs/SOLUCION-ERROR-POSTGRES.md](docs/SOLUCION-ERROR-POSTGRES.md)** (causa habitual: conexión al PostgreSQL equivocado o volumen Docker desfasado).

**Opción B — PostgreSQL instalado (script manual)**

Como superusuario (ej. `postgres`):

```powershell
psql -U postgres -h localhost -f scripts/postgres/setup-completo-manual.sql
```

Ajusta host, usuario y contraseñas del script en entornos reales.

### 4.2 Aplicaciones

```powershell
# Raíz — tests (H2 en perfil test; no requiere PostgreSQL)
mvn verify

# Servicio Flota (necesita PostgreSQL con esquema creado)
cd ms-flota-rest
mvn spring-boot:run

# Otra terminal — Taller (PostgreSQL + Flota en marcha)
cd ms-taller-rest
mvn spring-boot:run
```

### Pruebas rápidas con curl (PowerShell)

**Listar vehículos disponibles:**

```powershell
curl -s http://localhost:8081/api/v1/vehiculos/disponibles
```

**Consulta estilo taller (con Flota en marcha y datos demo):**

```powershell
curl -s http://localhost:8082/api/taller/v1/vehiculos/ABC1234
```

**Registrar orden de mantenimiento:**

```powershell
curl -s -X POST http://localhost:8082/api/taller/v1/ordenes-mantenimiento `
  -H "Content-Type: application/json" `
  -d "{\"matricula\":\"XYZ5678\",\"descripcion\":\"Revisión preventiva\"}"
```

Comprueba después en Flota que `XYZ5678` figura como `MANTENIMIENTO`.

---

## 5. GitHub: ramas, SonarCloud y Telegram

### 5.1 Ramas

Crea en GitHub las ramas **`main`** y **`development`** y sube este repositorio. El workflow ya filtra por esas ramas.

### 5.2 SonarCloud (análisis estático en cada push/PR)

1. Entra en [SonarCloud](https://sonarcloud.io) e inicia sesión con GitHub.
2. Crea una **organización** y un **proyecto** enlazado al repo (o usa análisis manual con Maven).
3. Obtén:
   - **Organization key**
   - **Project key**
4. En GitHub: **Settings → Secrets and variables → Actions**, añade:
   - `SONAR_TOKEN` — token de SonarCloud (Generate token).
   - `SONAR_ORGANIZATION` — clave de la organización.
   - `SONAR_PROJECT_KEY` — clave del proyecto (`org_proyecto`).

Si **cualquiera** falta, el workflow **omite** Sonar pero el build Maven **sigue ejecutándose**.

El archivo `sonar-project.properties` define cobertura JaCoCo en:

`ms-flota-rest/target/site/jacoco/jacoco.xml`  
`ms-taller-rest/target/site/jacoco/jacoco.xml`

### 5.3 Telegram — paso a paso (lo que pide la fase)

Objetivo: que el pipeline **publique un mensaje en un grupo (o chat)** con el resultado del CI.

#### A) Crear el bot

1. Abre Telegram y busca **@BotFather**.
2. Envía `/newbot` y sigue las instrucciones: nombre para mostrar y **username** terminado en `bot`.
3. BotFather devuelve el **HTTP API token** (parece `123456789:AAH...`). Ese valor es **`TELEGRAM_BOT_TOKEN`**.

#### B) Obtener el identificador del chat (grupo o usuario)

**Opción 1 — Grupo (recomendado para equipo de clase):**

1. Crea un grupo de Telegram e invita al **bot** al grupo (añadir miembros → buscar el username del bot).
2. Envía **cualquier mensaje** en el grupo (puede ser `/start @tu_bot`).
3. Abre en el navegador (sustituye `TOKEN`):

   `https://api.telegram.org/botTOKEN/getUpdates`

4. En el JSON, busca `"chat":{"id": -100xxxxxxxxxx }` — ese número (incluido el signo menos si es grupo supergrupo) es **`TELEGRAM_CHAT_ID`**.

**Opción 2 — Chat directo contigo:**

1. Escribe al bot en privado `/start`.
2. Misma URL `getUpdates` y usa `"chat":{"id": 123456789}` (positivo).

#### C) Secretos en GitHub

En **Settings → Secrets and variables → Actions**:

| Secreto | Valor |
|---------|--------|
| `TELEGRAM_BOT_TOKEN` | Token de BotFather |
| `TELEGRAM_CHAT_ID` | Id numérico del grupo o usuario |

#### D) Qué envía exactamente el workflow

Un texto en una sola línea con:

- nombre del repo;
- rama;
- resultado del job Maven (`success` / `failure` / …);
- **cobertura aproximada de instrucciones** según JaCoCo por módulo (porcentaje);
- si Sonar se ejecutó o se omitió; si se ejecutó, se indica que el detalle de **issues** está en el panel de SonarCloud;
- enlace a la ejecución de GitHub Actions.

Si no configuras Telegram, el paso **no falla** el pipeline: imprime en el log que Telegram quedó omitido.

---

## 6. Estructura de carpetas relevante

```
logiflow-fase1/
├── docker-compose.yml
├── pom.xml
├── sonar-project.properties
├── README.md
├── GUÍA-FASE1-LOGIFLOW.md          ← este archivo
├── scripts/postgres/
│   ├── GUIA-SCRIPTS-BASE-DE-DATOS.md  ← orden de scripts y carga de datos
│   ├── docker-init/                   ← init automático del contenedor (01…04)
│   └── setup-completo-manual.sql      ← instalación PostgreSQL “en seco”
├── .github/workflows/ci.yml
├── ms-flota-rest/
│   └── src/main/java/com/logiflow/flota/...
└── ms-taller-rest/
    ├── docs/contrato-taller.wsdl
    └── src/main/java/com/logiflow/taller/...
```

---

## 7. Próximos pasos (fuera de Fase 1)

- RabbitMQ y eventos (`pedido.creado`, …).
- GraphQL BFF y WebSockets en Seguimiento.
- Kubernetes y despliegue frontend en la nube.

Con lo anterior tienes **código ejecutable**, **documentación DDD** para el informe y **instrucciones reproducibles** para **Telegram** y **SonarCloud** alineadas con el enunciado de la Fase 1.
