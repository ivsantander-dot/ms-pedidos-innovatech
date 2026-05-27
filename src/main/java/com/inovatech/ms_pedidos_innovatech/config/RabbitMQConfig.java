package com.inovatech.ms_pedidos_innovatech.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${pedido.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${pedido.rabbitmq.routing-key}")
    private String routingKey;

    @Bean
    TopicExchange pedidoExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
