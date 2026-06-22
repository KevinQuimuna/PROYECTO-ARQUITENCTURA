package com.logiflow.ruteo.application;

import com.logiflow.common.events.EnvioAsignadoEvent;
import com.logiflow.common.events.PedidoEntregadoEvent;
import com.logiflow.common.events.PosicionActualizadaEvent;
import com.logiflow.common.events.RabbitNames;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RuteoEventPublisher {

    private final RabbitTemplate rabbit;

    public RuteoEventPublisher(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    public void envioAsignado(EnvioAsignadoEvent e) {
        rabbit.convertAndSend(RabbitNames.EXCHANGE, RabbitNames.RK_ENVIO_ASIGNADO, e);
    }

    public void pedidoEntregado(PedidoEntregadoEvent e) {
        rabbit.convertAndSend(RabbitNames.EXCHANGE, RabbitNames.RK_PEDIDO_ENTREGADO, e);
    }

    public void posicionActualizada(PosicionActualizadaEvent e) {
        rabbit.convertAndSend(RabbitNames.EXCHANGE, RabbitNames.RK_POSICION_ACTUALIZADA, e);
    }
}
