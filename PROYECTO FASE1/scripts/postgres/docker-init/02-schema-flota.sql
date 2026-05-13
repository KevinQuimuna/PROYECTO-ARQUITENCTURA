-- Esquema para ms-flota-rest (BD por defecto del contenedor: logiflow_flota)

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
