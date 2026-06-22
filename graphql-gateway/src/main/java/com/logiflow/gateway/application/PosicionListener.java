package com.logiflow.gateway.application;

import com.logiflow.common.events.PosicionActualizadaEvent;
import com.logiflow.gateway.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PosicionListener {

    private final PosicionStreamService streamService;

    public PosicionListener(PosicionStreamService streamService) {
        this.streamService = streamService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_POSICION)
    public void onPosicion(PosicionActualizadaEvent evt) {
        streamService.publicar(evt);
    }
}
