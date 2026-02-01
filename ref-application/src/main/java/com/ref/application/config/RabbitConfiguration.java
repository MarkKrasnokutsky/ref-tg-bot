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

    @PostConstruct
    public void init() {
        Exchange emissionEventExchange = rabbitUtils.configureExchange(rabbitProperty.getExchanges().getUserQrGenEvent().getName());

        rabbitUtils.configureQueue(
                rabbitProperty.getQueues().getUserQrGenQueue().getName(),
                emissionEventExchange
        );

        Exchange articleEventExchange = rabbitUtils.configureExchange(rabbitProperty.getExchanges().getUserQrGenEvent().getName());

        rabbitUtils.configureQueue(
                rabbitProperty.getQueues().getUserQrGenQueue().getName(),
                articleEventExchange
        );
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory,
                                         MessageConverter jacksonMessageConverter) {
        RabbitTemplate tpl = new RabbitTemplate(factory);
        tpl.setMessageConverter(jacksonMessageConverter);
        return tpl;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory factory,
            MessageConverter jacksonMessageConverter) {
        SimpleRabbitListenerContainerFactory f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(factory);
        f.setMessageConverter(jacksonMessageConverter);
        f.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return f;
    }
}
