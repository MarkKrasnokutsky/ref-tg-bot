package com.ref.application.config;

import com.mark.refrabbitmq.property.IRabbitProperty;
import com.mark.refrabbitmq.utils.RabbitUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitConfiguration {

    private final RabbitUtils rabbitUtils;
    private final IRabbitProperty rabbitProperty;

    /**
     * @PostConstruct - метод выполняется ПОСЛЕ создания бина и инъекции зависимостей,
     * но ДО того как бин будет доступен для использования.
     *
     * Здесь происходит НАСТРОЙКА ИНФРАСТРУКТУРЫ RabbitMQ:
     * 1. Создание Exchange (точка маршрутизации сообщений)
     * 2. Создание Queue (очереди сообщений)
     * 3. Привязка Queue к Exchange
     */
    @PostConstruct
    public void init() {
        // 🔄 Создание Exchange для событий генерации QR-кодов пользователями
        Exchange emissionEventExchange = rabbitUtils.configureExchange(rabbitProperty.getExchanges().getUserQrGenEvent().getName());

        // 📥 Создание Queue и привязка к Exchange
        // Эта очередь будет получать сообщения о генерации QR-кодов
        rabbitUtils.configureQueue(
                rabbitProperty.getQueues().getUserQrGenQueue().getName(),
                emissionEventExchange
        );
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 🎯 Бин: RabbitTemplate
     * Это ОСНОВНОЙ КЛИЕНТ для отправки сообщений в RabbitMQ
     * Аналогичен JdbcTemplate для базы данных
     *
     * @param factory - ConnectionFactory (автоматически настраивается Spring Boot из application.yml)
     * @param jacksonMessageConverter - конвертер созданный выше
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory,
                                         MessageConverter jacksonMessageConverter) {
        RabbitTemplate tpl = new RabbitTemplate(factory);
        tpl.setMessageConverter(jacksonMessageConverter);
        return tpl;
    }

    /**
     * 🎯 Бин: SimpleRabbitListenerContainerFactory
     * Фабрика для создания контейнеров, которые слушают (получают) сообщения из RabbitMQ
     *
     * Используется аннотацией @RabbitListener для методов-обработчиков сообщений
     *
     * @param factory - ConnectionFactory
     * @param jacksonMessageConverter - конвертер для JSON
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory factory,
            MessageConverter jacksonMessageConverter) {
        SimpleRabbitListenerContainerFactory f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(factory); // Источник соединений
        f.setMessageConverter(jacksonMessageConverter); // Конвертер сообщений
        f.setAcknowledgeMode(AcknowledgeMode.AUTO); // Автоподтверждение получения
        return f;
    }
}
