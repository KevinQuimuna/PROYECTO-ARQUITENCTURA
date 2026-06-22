package com.logiflow.ruteo.application;

import com.logiflow.common.events.PosicionActualizadaEvent;
import com.logiflow.ruteo.domain.Envio;
import com.logiflow.ruteo.domain.Parada;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PositionSimulator {

    private final EnvioService envioService;
    private final RuteoEventPublisher publisher;

    public PositionSimulator(EnvioService envioService, RuteoEventPublisher publisher) {
        this.envioService = envioService;
        this.publisher = publisher;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional(readOnly = true)
    public void emitir() {
        for (Envio e : envioService.listarEnRuta()) {
            Parada destino = e.getParadas().stream()
                    .filter(p -> "ENTREGA".equals(p.getTipo()))
                    .min(Comparator.comparingInt(Parada::getOrdenParada))
                    .orElse(null);
            if (destino == null) {
                continue;
            }
            double jitter = 0.0005 * ThreadLocalRandom.current().nextDouble(-1, 1);
            double lat = destino.getLat() + jitter;
            double lng = destino.getLng() + jitter;
            publisher.posicionActualizada(new PosicionActualizadaEvent(
                    e.getId(), e.getCodigoSeguimiento(), lat, lng, 35.0, "TRAMO_URBANO", e.getEtaMinutos()));
        }
    }
}
