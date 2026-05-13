-- Datos de demostración para logiflow_flota (después del esquema).
-- Idempotente: no inserta si la matrícula o la licencia ya existen.
-- Conexión esperada: base logiflow_flota (es la predeterminada del contenedor).

INSERT INTO vehiculos (matricula, tipo, capacidad_kg, autonomia_km, estado)
VALUES ('ABC1234', 'MOTO', 25, 120, 'DISPONIBLE')
ON CONFLICT (matricula) DO NOTHING;

INSERT INTO vehiculos (matricula, tipo, capacidad_kg, autonomia_km, estado)
VALUES ('XYZ5678', 'FURGONETA', 800, 450, 'DISPONIBLE')
ON CONFLICT (matricula) DO NOTHING;

INSERT INTO vehiculos (matricula, tipo, capacidad_kg, autonomia_km, estado)
VALUES ('CAM9999', 'CAMION', 12000, 800, 'MANTENIMIENTO')
ON CONFLICT (matricula) DO NOTHING;

-- Asignación conductores ↔ vehículos por matrícula (evita depender de ids fijos).
INSERT INTO conductores (nombre_completo, licencia, estado, vehiculo_id)
SELECT 'María López', 'LIC-001-EC', 'DISPONIBLE', v.id
FROM vehiculos v WHERE v.matricula = 'ABC1234'
ON CONFLICT (licencia) DO NOTHING;

INSERT INTO conductores (nombre_completo, licencia, estado, vehiculo_id)
SELECT 'Carlos Ruiz', 'LIC-002-EC', 'EN_SERVICIO', v.id
FROM vehiculos v WHERE v.matricula = 'XYZ5678'
ON CONFLICT (licencia) DO NOTHING;
