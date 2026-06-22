package com.logiflow.common.events;

public record PosicionActualizadaEvent(
        Long envioId,
        String codigoSeguimiento,
        double lat,
        double lng,
        Double velocidadKmh,
        String tramo,
        Integer etaMinutos) {}
