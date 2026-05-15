package com.logiflow.taller.api.dto;

import java.time.Instant;

public record RegistrarOrdenMantenimientoResponse(
        String idOrden, String matricula, Instant fechaRegistro, String mensaje) {}
