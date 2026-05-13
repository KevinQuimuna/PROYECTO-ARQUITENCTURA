package com.logiflow.flota.api.dto;

import com.logiflow.flota.domain.EstadoVehiculo;
import com.logiflow.flota.domain.TipoVehiculo;

public record VehiculoResponse(
        Long id,
        String matricula,
        TipoVehiculo tipo,
        Double capacidadKg,
        Integer autonomiaKm,
        EstadoVehiculo estado) {}
