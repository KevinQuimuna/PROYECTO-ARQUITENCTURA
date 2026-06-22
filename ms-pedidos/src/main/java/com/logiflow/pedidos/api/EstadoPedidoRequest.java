package com.logiflow.pedidos.api;

import com.logiflow.pedidos.domain.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public record EstadoPedidoRequest(@NotNull EstadoPedido estado) {}
