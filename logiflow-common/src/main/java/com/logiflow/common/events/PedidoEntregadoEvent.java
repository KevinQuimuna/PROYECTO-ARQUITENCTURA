package com.logiflow.common.events;

public record PedidoEntregadoEvent(
        Long pedidoId,
        String codigoSeguimiento,
        Long clienteId,
        Long envioId,
        String nivel,
        double pesoKg,
        String tipoVehiculo,
        double kmsRecorridos) {}
