\connect logiflow_clientes

CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(32) NOT NULL UNIQUE,
    razon_social VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    telefono VARCHAR(64),
    tipo VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL
);

CREATE TABLE cuentas_corporativas (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES clientes (id),
    nombre_cuenta VARCHAR(255) NOT NULL,
    contrato_numero VARCHAR(64),
    saldo NUMERIC(14, 2) NOT NULL DEFAULT 0,
    limite_credito NUMERIC(14, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_cuentas_cliente ON cuentas_corporativas (cliente_id);

ALTER TABLE clientes OWNER TO logiflow_clientes;
ALTER TABLE cuentas_corporativas OWNER TO logiflow_clientes;
