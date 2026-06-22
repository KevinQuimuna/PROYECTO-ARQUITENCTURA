package com.logiflow.pedidos.api;

import com.logiflow.pedidos.domain.NivelGeografico;
import com.logiflow.pedidos.domain.PrioridadPedido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record UpsertPedidoRequest(
        @NotNull Long clienteId,
        @NotBlank String origenDireccion,
        Double origenLat,
        Double origenLng,
        @NotBlank String destinoDireccion,
        Double destinoLat,
        Double destinoLng,
        @NotNull BigDecimal pesoKg,
        @NotNull NivelGeografico nivel,
        PrioridadPedido prioridad,
        List<PaqueteLineRequest> paquetes) {}
