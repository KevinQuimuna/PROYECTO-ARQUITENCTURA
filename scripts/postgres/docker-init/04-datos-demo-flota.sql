INSERT INTO vehiculos (id, matricula, tipo, capacidad_kg, autonomia_km, estado)
VALUES
    ('VEH000000001', 'ABC1234', 'MOTO',     25,    120, 'DISPONIBLE'),
    ('VEH000000002', 'XYZ5678', 'FURGONETA', 800,  450, 'DISPONIBLE'),
    ('VEH000000003', 'CAM9999', 'CAMION',   12000, 800, 'MANTENIMIENTO')
    ON CONFLICT (matricula) DO NOTHING;

INSERT INTO conductores (id, nombre_completo, licencia, estado, vehiculo_id)
VALUES
    ('CON000000001', 'María López', 'LIC-001-EC', 'DISPONIBLE',  'VEH000000001'),
    ('CON000000002', 'Carlos Ruiz', 'LIC-002-EC', 'EN_SERVICIO', 'VEH000000002')
    ON CONFLICT (licencia) DO NOTHING;