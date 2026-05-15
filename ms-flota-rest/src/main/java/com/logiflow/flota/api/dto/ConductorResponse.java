package com.logiflow.flota.api.dto;

import com.logiflow.flota.domain.EstadoConductor;

public record ConductorResponse(
        String id, String nombreCompleto, String licencia, String vehiculoId, String matriculaVehiculo, EstadoConductor estado) {}
