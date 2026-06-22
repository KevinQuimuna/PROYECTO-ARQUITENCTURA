package com.logiflow.pedidos.application;

import com.logiflow.common.events.PedidoCanceladoEvent;
import com.logiflow.common.events.PedidoCreadoEvent;
import com.logiflow.common.events.RabbitNames;
import com.logiflow.pedidos.domain.Pedido;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PedidoEventPublisher {

    private final RabbitTemplate rabbit;

    public PedidoEventPublisher(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    public void publicarCreado(Pedido p) {
        var evt = new PedidoCreadoEvent(
                p.getId(),
                p.getCodigoSeguimiento(),
                p.getClienteId(),
                p.getNivel().name(),
                p.getPesoKg().doubleValue(),
                p.getDestinoLat(),
                p.getDestinoLng());
        rabbit.convertAndSend(RabbitNames.EXCHANGE, RabbitNames.RK_PEDIDO_CREADO, evt);
    }

    public void publicarCancelado(Pedido p) {
        rabbit.convertAndSend(
                RabbitNames.EXCHANGE,
                RabbitNames.RK_PEDIDO_CANCELADO,
                new PedidoCanceladoEvent(p.getId(), p.getCodigoSeguimiento()));
    }
}
