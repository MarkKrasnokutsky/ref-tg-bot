package com.ref.application.service.manager;

import com.mark.backend_application.domain.response.QrCodeResponse;
import com.mark.refrabbitmq.domain.message.IndQrInfoMessage;
import com.ref.application.config.TelegramProps;
import com.ref.application.domain.entity.UserEntity;
import com.ref.application.repository.UserRepository;
import com.ref.application.service.IRefBackendApiService;
import com.ref.application.service.message.IEventUserQrGenMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.ByteArrayInputStream;


@Component
public class QrManager {

    private final IEventUserQrGenMessageService eventUserQrGenMessageService;
    private final UserRepository userRepository;
    private final IRefBackendApiService refBackendApiService;
    private final TelegramProps telegramProps;
    private final TelegramClient telegramClient;

    public QrManager(IEventUserQrGenMessageService eventUserQrGenMessageService, UserRepository userRepository, IRefBackendApiService refBackendApiService,
                     TelegramProps telegramProps) {
        this.eventUserQrGenMessageService = eventUserQrGenMessageService;
        this.userRepository = userRepository;
        this.refBackendApiService = refBackendApiService;
        this.telegramProps = telegramProps;
        this.telegramClient = new OkHttpTelegramClient(telegramProps.getToken());
    }

    public void answerCommand(Message message) {

        UserEntity currentUser = userRepository.findByChatId(message.getChatId());

        eventUserQrGenMessageService.sendUserQrGenToQueue(IndQrInfoMessage.builder()
                .balance(currentUser.getBalance())
                .username(currentUser.getUsername())
                .userId(currentUser.getUserId())
                .build());
        QrCodeResponse qrCodeResponse = refBackendApiService.getQRCodeInfoByOwnerId(message.getChatId());
        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(message.getChatId())
                .photo(new InputFile(
                                new ByteArrayInputStream(refBackendApiService.getImageQrByOwnerId(message.getChatId())),
                                qrCodeResponse.getFileName() != null ? qrCodeResponse.getFileName() : "article.jpg"
                ))
                .build();
        try {
            telegramClient.execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
