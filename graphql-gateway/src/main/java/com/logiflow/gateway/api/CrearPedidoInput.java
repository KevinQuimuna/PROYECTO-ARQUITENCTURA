package com.logiflow.gateway.api;

public record CrearPedidoInput(
        Integer clienteId,
        String origenDireccion,
        Double origenLat,
        Double origenLng,
        String destinoDireccion,
        Double destinoLat,
        Double destinoLng,
        Double pesoKg,
        String nivel,
        String prioridad) {}
