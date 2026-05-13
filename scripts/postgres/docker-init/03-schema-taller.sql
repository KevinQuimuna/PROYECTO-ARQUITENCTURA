-- Esquema para ms-taller-rest (conexión a la BD logiflow_taller)
\connect logiflow_taller

CREATE TABLE ordenes_mantenimiento (
    id BIGSERIAL PRIMARY KEY,
    matricula VARCHAR(32) NOT NULL,
    descripcion VARCHAR(2000) NOT NULL,
    fecha_registro TIMESTAMP(6) WITH TIME ZONE NOT NULL
);

ALTER TABLE ordenes_mantenimiento OWNER TO logiflow_taller;
ALTER SEQUENCE ordenes_mantenimiento_id_seq OWNER TO logiflow_taller;
