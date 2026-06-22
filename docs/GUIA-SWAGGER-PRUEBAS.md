# Guía rápida — Swagger y pruebas LogiFlow

## Requisito previo

```bash
docker compose up -d
```

PostgreSQL debe estar en **localhost:5433**. Si los GET fallan con error de base de datos, reinicie el contenedor o recree el volumen:

```bash
docker compose down -v
docker compose up -d
```

---

## URLs de Swagger UI (interfaz de pruebas)

| Microservicio | Swagger UI | OpenAPI JSON |
|---------------|------------|--------------|
| **ms-flota-rest** | http://localhost:8081/swagger-ui.html | http://localhost:8081/api-docs |
| **ms-taller-rest** | http://localhost:8082/swagger-ui.html | http://localhost:8082/api-docs |
| **ms-auth** | http://localhost:8083/swagger-ui.html | http://localhost:8083/api-docs |
| **ms-clientes** | http://localhost:8084/swagger-ui.html | http://localhost:8084/api-docs |
| **ms-pedidos** | http://localhost:8085/swagger-ui.html | http://localhost:8085/api-docs |
| **ms-ruteo** | http://localhost:8086/swagger-ui.html | http://localhost:8086/api-docs |

**GraphQL (no Swagger):** http://localhost:8088/graphiql

**RabbitMQ UI:** http://localhost:15672 (usuario: `logiflow`, contraseña: `logiflow`)

---

## Arrancar cada servicio

```bash
mvn -pl ms-clientes spring-boot:run
mvn -pl ms-auth spring-boot:run
# ... etc.
```

---

## Pruebas recomendadas por servicio

### ms-clientes (8084)

1. `GET /api/clientes/health` → debe responder `{"status":"UP"}`
2. `POST /api/clientes` — **no envíe id ni codigo**:

```json
{
  "razonSocial": "Mi Empresa S.A.",
  "email": "info@miempresa.test",
  "telefono": "+5491100000000",
  "tipo": "CORPORATIVO",
  "activo": true
}
```

3. `GET /api/clientes` → lista con el cliente creado (id y codigo autogenerados)

### ms-auth (8083)

Usuario demo: **admin** / **admin123**

1. `GET /api/auth/health`
2. `POST /api/auth/login` o `POST /login`:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

3. Copie el `token` de la respuesta y use `POST /api/auth/verify`:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIs..."
}
```

### ms-pedidos (8085)

Requiere `clienteId` existente (creado en ms-clientes):

```json
{
  "clienteId": 1,
  "origenDireccion": "Av. Corrientes 1000",
  "destinoDireccion": "Av. Santa Fe 2000",
  "pesoKg": 5.5,
  "nivel": "URBANO"
}
```

---

## Errores frecuentes

| Síntoma | Causa | Solución |
|---------|-------|----------|
| GET devuelve 503 / DATABASE | PostgreSQL no corre | `docker compose up -d` |
| Swagger en puerto incorrecto | Confusión de puertos Fase 1 vs 2 | ms-clientes = **8084**, no 8082 |
| Login 401 | Usuario incorrecto o BD vacía | Usar admin/admin123; reiniciar ms-auth |
| Register 400 Rol inválido | Rol inexistente | Omitir `role` o usar `ROLE_USER` |
| Auth solo tiene POST | Es normal | Login/verify son POST, no GET |
