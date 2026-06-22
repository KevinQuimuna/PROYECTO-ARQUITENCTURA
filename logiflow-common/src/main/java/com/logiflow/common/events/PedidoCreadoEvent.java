package com.logiflow.common.events;

public record PedidoCreadoEvent(
        Long pedidoId,
        String codigoSeguimiento,
        Long clienteId,
        String nivel,
        double pesoKg,
        Double destinoLat,
        Double destinoLng) {}
