package com.mark.refrabbitmq.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitUtils {

    private final AmqpAdmin amqpAdmin;

    /**
     * 📡 Создание и настройка Exchange (точки маршрутизации сообщений)
     *
     * Exchange - это "почтовое отделение" в RabbitMQ:
     * - Принимает сообщения от продюсеров
     * - Решает, в какие очереди отправить сообщение
     *
     * @param exchangeName - имя Exchange (например: "user.qr.gen.event")
     * @return созданный Exchange
     */
    public Exchange configureExchange(String exchangeName) {
        Exchange exchange = ExchangeBuilder
                .directExchange(exchangeName)
                .durable(true)
                .build();
        amqpAdmin.declareExchange(exchange);
        return exchange;
    }

    /**
     * 📥 Создание и настройка Queue (очереди) с привязкой к Exchange
     *
     * Queue - это "почтовый ящик" в RabbitMQ:
     * - Хранит сообщения до их обработки потребителем
     * - Каждая Queue привязана к одному или нескольким Exchange
     *
     * @param queueName - имя очереди (например: "user.qr.gen.queue")
     * @param exchange - Exchange, к которому привязываем очередь
     */
    public void configureQueue(String queueName, Exchange exchange) {
        Queue queue = QueueBuilder.durable(queueName).build();
        doConfigureQueue(queue, exchange);
    }

    /**
     * 🔐 Приватный метод для полной настройки очереди
     *
     * 1. Регистрирует Queue в RabbitMQ
     * 2. Создает Binding (привязку) между Queue и Exchange
     * 3. Регистрирует Binding в RabbitMQ
     *
     * @param queue - созданная очередь
     * @param exchange - exchange, к которому привязываем
     */
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
