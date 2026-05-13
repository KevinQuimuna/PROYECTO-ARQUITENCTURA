package com.logiflow.flota.api.dto;

import com.logiflow.flota.domain.EstadoVehiculo;
import com.logiflow.flota.domain.TipoVehiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record VehiculoRequest(
        @NotBlank String matricula,
        @NotNull TipoVehiculo tipo,
        @NotNull @PositiveOrZero Double capacidadKg,
        @PositiveOrZero Integer autonomiaKm,
        @NotNull EstadoVehiculo estado) {}
