package com.logiflow.ruteo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logiflow.common.events.RabbitNames;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE_PEDIDO_CREADO = "ms_ruteo.pedido.creado";
    public static final String QUEUE_PEDIDO_CANCELADO = "ms_ruteo.pedido.cancelado";

    @Bean
    public TopicExchange logiflowEvents() {
        return new TopicExchange(RabbitNames.EXCHANGE, true, false);
    }

    @Bean
    public Queue ruteoPedidoCreadoQueue() {
        return new Queue(QUEUE_PEDIDO_CREADO, true);
    }

    @Bean
    public Queue ruteoPedidoCanceladoQueue() {
        return new Queue(QUEUE_PEDIDO_CANCELADO, true);
    }

    @Bean
    public Binding bindPedidoCreado(Queue ruteoPedidoCreadoQueue, TopicExchange logiflowEvents) {
        return BindingBuilder.bind(ruteoPedidoCreadoQueue).to(logiflowEvents).with(RabbitNames.RK_PEDIDO_CREADO);
    }

    @Bean
    public Binding bindPedidoCancelado(Queue ruteoPedidoCanceladoQueue, TopicExchange logiflowEvents) {
        return BindingBuilder.bind(ruteoPedidoCanceladoQueue).to(logiflowEvents).with(RabbitNames.RK_PEDIDO_CANCELADO);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, Jackson2JsonMessageConverter conv) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(conv);
        return t;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory cf, Jackson2JsonMessageConverter conv) {
        SimpleRabbitListenerContainerFactory f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(cf);
        f.setMessageConverter(conv);
        return f;
    }
}
