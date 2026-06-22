package com.logiflow.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logiflow.common.events.RabbitNames;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE_POSICION = "graphql_gateway.posicion";

    @Bean
    public TopicExchange logiflowEvents() {
        return new TopicExchange(RabbitNames.EXCHANGE, true, false);
    }

    @Bean
    public Queue posicionQueue() {
        return new Queue(QUEUE_POSICION, true);
    }

    @Bean
    public Binding bindPosicion(Queue posicionQueue, TopicExchange logiflowEvents) {
        return BindingBuilder.bind(posicionQueue).to(logiflowEvents).with(RabbitNames.RK_POSICION_ACTUALIZADA);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
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
