-- Ejecutado automáticamente al crear el contenedor (solo la primera vez).
-- Crea la segunda base y el usuario del microservicio Taller.
CREATE USER logiflow_taller WITH PASSWORD 'taller_secret';
CREATE DATABASE logiflow_taller OWNER logiflow_taller;
GRANT ALL PRIVILEGES ON DATABASE logiflow_taller TO logiflow_taller;
