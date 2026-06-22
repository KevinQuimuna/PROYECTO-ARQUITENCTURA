\connect logiflow_pedidos

CREATE TABLE pedidos (
    id BIGSERIAL PRIMARY KEY,
    codigo_seguimiento VARCHAR(32) NOT NULL UNIQUE,
    cliente_id BIGINT NOT NULL,
    origen_direccion VARCHAR(512) NOT NULL,
    origen_lat DOUBLE PRECISION,
    origen_lng DOUBLE PRECISION,
    destino_direccion VARCHAR(512) NOT NULL,
    destino_lat DOUBLE PRECISION,
    destino_lng DOUBLE PRECISION,
    peso_kg NUMERIC(10, 2) NOT NULL,
    nivel VARCHAR(20) NOT NULL,
    prioridad VARCHAR(16) NOT NULL DEFAULT 'MEDIA',
    estado VARCHAR(20) NOT NULL DEFAULT 'CREADO',
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL
);

CREATE TABLE paquetes (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL REFERENCES pedidos (id),
    descripcion VARCHAR(512),
    peso_kg NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_pedidos_cliente ON pedidos (cliente_id);
CREATE INDEX idx_paquetes_pedido ON paquetes (pedido_id);

ALTER TABLE pedidos OWNER TO logiflow_pedidos;
ALTER TABLE paquetes OWNER TO logiflow_pedidos;
