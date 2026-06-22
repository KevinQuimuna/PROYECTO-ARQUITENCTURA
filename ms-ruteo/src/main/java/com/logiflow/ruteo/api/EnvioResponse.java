package com.logiflow.ruteo.api;

import com.logiflow.ruteo.domain.Envio;
import com.logiflow.ruteo.domain.EstadoEnvio;
import java.math.BigDecimal;

public record EnvioResponse(
        Long id,
        Long pedidoId,
        String codigoSeguimiento,
        String vehiculoId,
        String conductorId,
        String tipoVehiculo,
        EstadoEnvio estado,
        BigDecimal kmsEstimados,
        Integer etaMinutos,
        String rutaResumen) {

    public static EnvioResponse from(Envio e) {
        return new EnvioResponse(
                e.getId(),
                e.getPedidoId(),
                e.getCodigoSeguimiento(),
                e.getVehiculoId(),
                e.getConductorId(),
                e.getTipoVehiculo(),
                e.getEstado(),
                e.getKmsEstimados(),
                e.getEtaMinutos(),
                e.getRutaResumen());
    }
}
