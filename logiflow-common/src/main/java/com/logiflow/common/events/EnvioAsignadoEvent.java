package com.logiflow.common.events;

public record EnvioAsignadoEvent(
        Long envioId,
        Long pedidoId,
        String codigoSeguimiento,
        String vehiculoId,
        String conductorId,
        String tipoVehiculo,
        Double kmsEstimados,
        Integer etaMinutos) {}
