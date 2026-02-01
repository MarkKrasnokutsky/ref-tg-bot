package com.mark.refrabbitmq.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitUtils {

    private final AmqpAdmin amqpAdmin;

    public Exchange configureExchange(String exchangeName) {
        Exchange exchange = ExchangeBuilder
                .directExchange(exchangeName)
                .durable(true)
                .build();
        amqpAdmin.declareExchange(exchange);
        return exchange;
    }

    public void configureQueue(String queueName, Exchange exchange) {
        Queue queue = QueueBuilder.durable(queueName).build();
        doConfigureQueue(queue, exchange);
    }

    private void doConfigureQueue(Queue queue, Exchange exchange) {
        amqpAdmin.declareQueue(queue);
        Binding binding = BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(queue.getName())
                .noargs();
        amqpAdmin.declareBinding(binding);
    }

}
