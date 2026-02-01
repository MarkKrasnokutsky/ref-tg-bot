package com.mark.backend_application.service.listener;

import com.mark.backend_application.domain.request.QrCodeRequest;
import com.mark.backend_application.service.QrCodeService;
import com.mark.refrabbitmq.domain.message.IndQrInfoMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class QrCodeGenListener {

    private final QrCodeService qrCodeService;

    @RabbitListener(queues = "${rabbitmq.queues.user-qr-gen-queue.name}",
    messageConverter = "jsonMessageConverter")
    public void generateUserQr(IndQrInfoMessage msg) {
        log.debug("[IndQrInfoMessage] Пришло сообщение на обработку: {}", msg);
        log.info("Получен запрос на генерацию qr-кода пользователя {}", msg.getUsername());

        qrCodeService.generateQRCode(QrCodeRequest.builder()
                .userId(msg.getUserId())
                .balance(msg.getBalance())
                .username(msg.getUsername())
                .data(Map.of("type", "individual", "url", "example.com"))
                .build());
    }

}
