-- Bases de datos y usuarios para microservicios Fase 2
CREATE USER logiflow_clientes WITH PASSWORD 'clientes_secret';
CREATE DATABASE logiflow_clientes OWNER logiflow_clientes;
GRANT ALL PRIVILEGES ON DATABASE logiflow_clientes TO logiflow_clientes;

CREATE USER logiflow_pedidos WITH PASSWORD 'pedidos_secret';
CREATE DATABASE logiflow_pedidos OWNER logiflow_pedidos;
GRANT ALL PRIVILEGES ON DATABASE logiflow_pedidos TO logiflow_pedidos;

CREATE USER logiflow_auth WITH PASSWORD 'auth_secret';
CREATE DATABASE logiflow_auth OWNER logiflow_auth;
GRANT ALL PRIVILEGES ON DATABASE logiflow_auth TO logiflow_auth;

CREATE USER logiflow_ruteo WITH PASSWORD 'ruteo_secret';
CREATE DATABASE logiflow_ruteo OWNER logiflow_ruteo;
GRANT ALL PRIVILEGES ON DATABASE logiflow_ruteo TO logiflow_ruteo;
