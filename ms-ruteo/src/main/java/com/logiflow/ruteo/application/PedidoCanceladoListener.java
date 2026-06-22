package com.logiflow.ruteo.application;

import com.logiflow.common.events.PedidoCanceladoEvent;
import com.logiflow.ruteo.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PedidoCanceladoListener {

    private final EnvioService envioService;

    public PedidoCanceladoListener(EnvioService envioService) {
        this.envioService = envioService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_PEDIDO_CANCELADO)
    public void onPedidoCancelado(PedidoCanceladoEvent event) {
        envioService.procesarPedidoCancelado(event.pedidoId());
    }
}
