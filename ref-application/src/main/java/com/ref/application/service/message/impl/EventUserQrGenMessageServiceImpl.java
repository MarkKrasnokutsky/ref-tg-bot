package com.ref.application.service.message.impl;

import com.mark.refrabbitmq.domain.message.IndQrInfoMessage;
import com.mark.refrabbitmq.property.IRabbitProperty;
import com.ref.application.service.message.IEventUserQrGenMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventUserQrGenMessageServiceImpl implements IEventUserQrGenMessageService {

    private final AmqpTemplate amqpTemplate;
    private final IRabbitProperty rabbitProperty;

    @Override
    public void sendUserQrGenToQueue(IndQrInfoMessage msg) {
        log.info("Отправка запроса на генерацию QR-кода участника {} реферальной программы Trinity",
                msg.getUsername());
        amqpTemplate.convertAndSend(
                rabbitProperty.getExchanges().getUserQrGenEvent().getName(),
                rabbitProperty.getQueues().getUserQrGenQueue().getName(),
                msg
        );
    }
}
