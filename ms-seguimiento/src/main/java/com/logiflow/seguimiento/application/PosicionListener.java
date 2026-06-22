package com.logiflow.seguimiento.application;

import com.logiflow.common.events.PosicionActualizadaEvent;
import com.logiflow.seguimiento.config.RabbitConfig;
import java.util.Map;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class PosicionListener {

    private final SimpMessagingTemplate messaging;

    public PosicionListener(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_POSICION)
    public void onPosicion(PosicionActualizadaEvent evt) {
        String destino = "/topic/seguimiento/" + evt.codigoSeguimiento();
        messaging.convertAndSend(
                destino,
                Map.of(
                        "envioId", evt.envioId(),
                        "codigoSeguimiento", evt.codigoSeguimiento(),
                        "lat", evt.lat(),
                        "lng", evt.lng(),
                        "velocidadKmh", evt.velocidadKmh() != null ? evt.velocidadKmh() : 0,
                        "tramo", evt.tramo() != null ? evt.tramo() : "",
                        "etaMinutos", evt.etaMinutos() != null ? evt.etaMinutos() : 0));
    }
}
