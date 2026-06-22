package com.logiflow.pedidos.api;

import com.logiflow.pedidos.domain.EstadoPedido;
import com.logiflow.pedidos.domain.NivelGeografico;
import com.logiflow.pedidos.domain.Pedido;
import com.logiflow.pedidos.domain.PrioridadPedido;
import java.math.BigDecimal;
import java.time.Instant;

public record PedidoResponse(
        Long id,
        String codigoSeguimiento,
        Long clienteId,
        String origenDireccion,
        Double origenLat,
        Double origenLng,
        String destinoDireccion,
        Double destinoLat,
        Double destinoLng,
        BigDecimal pesoKg,
        NivelGeografico nivel,
        PrioridadPedido prioridad,
        EstadoPedido estado,
        Instant createdAt,
        Instant updatedAt) {

    public static PedidoResponse from(Pedido p) {
        return new PedidoResponse(
                p.getId(),
                p.getCodigoSeguimiento(),
                p.getClienteId(),
                p.getOrigenDireccion(),
                p.getOrigenLat(),
                p.getOrigenLng(),
                p.getDestinoDireccion(),
                p.getDestinoLat(),
                p.getDestinoLng(),
                p.getPesoKg(),
                p.getNivel(),
                p.getPrioridad(),
                p.getEstado(),
                p.getCreatedAt(),
                p.getUpdatedAt());
    }
}
