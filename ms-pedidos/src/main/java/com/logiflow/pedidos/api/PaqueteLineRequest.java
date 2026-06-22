package com.logiflow.pedidos.api;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PaqueteLineRequest(String descripcion, @NotNull BigDecimal pesoKg) {}
