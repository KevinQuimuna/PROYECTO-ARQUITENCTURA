package com.logiflow.ruteo.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AsignacionManualRequest(@NotNull Long pedidoId, @NotBlank String vehiculoId) {}
