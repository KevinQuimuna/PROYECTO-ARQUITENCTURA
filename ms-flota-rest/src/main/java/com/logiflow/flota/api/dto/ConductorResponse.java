package com.logiflow.flota.api.dto;

import com.logiflow.flota.domain.EstadoConductor;

public record ConductorResponse(
        Long id, String nombreCompleto, String licencia, Long vehiculoId, String matriculaVehiculo, EstadoConductor estado) {}
