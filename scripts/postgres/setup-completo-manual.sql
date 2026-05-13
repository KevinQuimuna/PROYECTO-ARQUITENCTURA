-- =============================================================================
-- LogiFlow — script manual para PostgreSQL (fuera de Docker)
-- =============================================================================
-- Ejecutar como superusuario (por ejemplo el rol "postgres"):
--   psql -U postgres -h localhost -f setup-completo-manual.sql
--
-- Ajusta contraseñas en producción y actualiza variables SPRING_DATASOURCE_*.
-- =============================================================================

CREATE USER logiflow_flota WITH PASSWORD 'flota_secret';
CREATE USER logiflow_taller WITH PASSWORD 'taller_secret';

CREATE DATABASE logiflow_flota OWNER logiflow_flota;
CREATE DATABASE logiflow_taller OWNER logiflow_taller;

GRANT ALL PRIVILEGES ON DATABASE logiflow_flota TO logiflow_flota;
GRANT ALL PRIVILEGES ON DATABASE logiflow_taller TO logiflow_taller;

\connect logiflow_flota

CREATE TABLE vehiculos (
    id BIGSERIAL PRIMARY KEY,
    matricula VARCHAR(32) NOT NULL,
    tipo VARCHAR(255) NOT NULL,
    capacidad_kg DOUBLE PRECISION NOT NULL,
    autonomia_km INTEGER,
    estado VARCHAR(255) NOT NULL,
    CONSTRAINT uk_vehiculos_matricula UNIQUE (matricula)
);

CREATE TABLE conductores (
    id BIGSERIAL PRIMARY KEY,
    nombre_completo VARCHAR(120) NOT NULL,
    licencia VARCHAR(64) NOT NULL,
    estado VARCHAR(255) NOT NULL,
    vehiculo_id BIGINT,
    CONSTRAINT uk_conductores_licencia UNIQUE (licencia),
    CONSTRAINT fk_conductores_vehiculo FOREIGN KEY (vehiculo_id) REFERENCES vehiculos (id)
);

CREATE INDEX idx_conductores_vehiculo_id ON conductores (vehiculo_id);

ALTER TABLE vehiculos OWNER TO logiflow_flota;
ALTER TABLE conductores OWNER TO logiflow_flota;
ALTER SEQUENCE vehiculos_id_seq OWNER TO logiflow_flota;
ALTER SEQUENCE conductores_id_seq OWNER TO logiflow_flota;

\connect logiflow_taller

CREATE TABLE ordenes_mantenimiento (
    id BIGSERIAL PRIMARY KEY,
    matricula VARCHAR(32) NOT NULL,
    descripcion VARCHAR(2000) NOT NULL,
    fecha_registro TIMESTAMP(6) WITH TIME ZONE NOT NULL
);

ALTER TABLE ordenes_mantenimiento OWNER TO logiflow_taller;
ALTER SEQUENCE ordenes_mantenimiento_id_seq OWNER TO logiflow_taller;
