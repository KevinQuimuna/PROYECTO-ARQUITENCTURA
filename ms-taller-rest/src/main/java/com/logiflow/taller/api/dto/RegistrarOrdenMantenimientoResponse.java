package com.logiflow.taller.api.dto;

import java.time.Instant;

public record RegistrarOrdenMantenimientoResponse(
        Long idOrden, String matricula, Instant fechaRegistro, String mensaje) {}
