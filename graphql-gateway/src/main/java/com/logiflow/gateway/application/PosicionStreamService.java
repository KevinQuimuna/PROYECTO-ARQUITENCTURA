package com.logiflow.gateway.application;

import com.logiflow.common.events.PosicionActualizadaEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class PosicionStreamService {

    private final Map<String, PosicionActualizadaEvent> ultimasPorCodigo = new ConcurrentHashMap<>();
    private final Map<Long, PosicionActualizadaEvent> ultimasPorEnvio = new ConcurrentHashMap<>();
    private final Sinks.Many<PosicionActualizadaEvent> sink =
            Sinks.many().multicast().onBackpressureBuffer();

    public void publicar(PosicionActualizadaEvent evt) {
        ultimasPorCodigo.put(evt.codigoSeguimiento(), evt);
        ultimasPorEnvio.put(evt.envioId(), evt);
        sink.tryEmitNext(evt);
    }

    public PosicionActualizadaEvent ultimaPorEnvio(Long envioId) {
        return ultimasPorEnvio.get(envioId);
    }

    public Flux<Map<String, Object>> stream(String codigoSeguimiento) {
        return sink.asFlux()
                .filter(e -> codigoSeguimiento.equals(e.codigoSeguimiento()))
                .map(this::toMap);
    }

    public Map<String, Object> toMap(PosicionActualizadaEvent evt) {
        return Map.of(
                "envioId", evt.envioId(),
                "codigoSeguimiento", evt.codigoSeguimiento(),
                "lat", evt.lat(),
                "lng", evt.lng(),
                "velocidadKmh", evt.velocidadKmh() != null ? evt.velocidadKmh() : 0.0,
                "tramo", evt.tramo() != null ? evt.tramo() : "",
                "etaMinutos", evt.etaMinutos() != null ? evt.etaMinutos() : 0);
    }
}
