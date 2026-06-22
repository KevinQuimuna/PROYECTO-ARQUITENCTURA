\connect logiflow_ruteo

CREATE TABLE envios (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL UNIQUE,
    codigo_seguimiento VARCHAR(32) NOT NULL,
    vehiculo_id VARCHAR(12),
    conductor_id VARCHAR(12),
    tipo_vehiculo VARCHAR(20),
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    kms_estimados NUMERIC(10, 2),
    eta_minutos INTEGER,
    ruta_resumen VARCHAR(1024),
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL
);

CREATE TABLE paradas (
    id BIGSERIAL PRIMARY KEY,
    envio_id BIGINT NOT NULL REFERENCES envios (id),
    orden_parada INTEGER NOT NULL,
    lat DOUBLE PRECISION NOT NULL,
    lng DOUBLE PRECISION NOT NULL,
    tipo VARCHAR(32) NOT NULL DEFAULT 'ENTREGA'
);

CREATE INDEX idx_envios_codigo ON envios (codigo_seguimiento);
CREATE INDEX idx_paradas_envio ON paradas (envio_id);

ALTER TABLE envios OWNER TO logiflow_ruteo;
ALTER TABLE paradas OWNER TO logiflow_ruteo;
