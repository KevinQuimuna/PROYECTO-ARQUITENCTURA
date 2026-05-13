package com.logiflow.flota.api.dto;

import com.logiflow.flota.domain.EstadoConductor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConductorRequest(
        @NotBlank String nombreCompleto,
        @NotBlank String licencia,
        Long vehiculoId,
        @NotNull EstadoConductor estado) {}
