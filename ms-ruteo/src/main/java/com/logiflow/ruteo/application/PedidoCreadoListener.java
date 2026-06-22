package com.logiflow.ruteo.application;

import com.logiflow.common.events.PedidoCreadoEvent;
import com.logiflow.ruteo.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PedidoCreadoListener {

    private final EnvioService envioService;

    public PedidoCreadoListener(EnvioService envioService) {
        this.envioService = envioService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_PEDIDO_CREADO)
    public void onPedidoCreado(PedidoCreadoEvent event) {
        envioService.procesarPedidoCreado(event);
    }
}
