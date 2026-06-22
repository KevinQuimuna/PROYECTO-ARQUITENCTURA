\connect logiflow_clientes

INSERT INTO clientes (codigo, razon_social, email, telefono, tipo, activo, created_at, updated_at)
VALUES
    ('CLI-001', 'Acme Corp S.A.', 'contacto@acme.test', '+5491100000001', 'CORPORATIVO', TRUE, NOW(), NOW()),
    ('CLI-002', 'Juan Pérez', 'juan.perez@test.com', '+5491100000002', 'PARTICULAR', TRUE, NOW(), NOW());

INSERT INTO cuentas_corporativas (cliente_id, nombre_cuenta, contrato_numero, saldo, limite_credito, created_at)
SELECT id, 'Cuenta principal', 'CTR-2026-001', 15000.00, 50000.00, NOW()
FROM clientes WHERE codigo = 'CLI-001';

\connect logiflow_auth

INSERT INTO roles (name) VALUES ('ROLE_USER') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_ADMIN') ON CONFLICT (name) DO NOTHING;
